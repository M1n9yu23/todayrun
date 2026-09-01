package com.gyugle.gyurun.feature.active.presentation.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunDraft
import com.gyugle.gyurun.core.domain.run.repository.RunDraftRepository
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.domain.weather.repository.WeatherRepository
import com.gyugle.gyurun.core.presentation.ui.toUiText
import com.gyugle.gyurun.run.domain.ActiveRunServiceController
import com.gyugle.gyurun.run.domain.LocationDataCalculator
import com.gyugle.gyurun.run.domain.RunningTracker
import com.gyugle.gyurun.run.domain.WatchConnector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val weatherRepository: WeatherRepository,
    private val watchConnector: WatchConnector,
    private val runDraftRepository: RunDraftRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val activeRunServiceController: ActiveRunServiceController,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private var draftToResume: RunDraft? = null

    private var hasRequestedStepsPermission = false

    init {
        runningTracker.startObservingLocation()

        runningTracker.currentLocation
            .onEach { locationWithAltitude ->
                state = state.copy(currentLocation = locationWithAltitude?.location)
            }.launchIn(viewModelScope)

        runningTracker.runData
            .onEach { runData ->
                state = state.copy(runData = runData)
            }.launchIn(viewModelScope)

        runningTracker.elapsedTime
            .onEach { elapsedTime ->
                state = state.copy(elapsedTime = elapsedTime)
            }.launchIn(viewModelScope)

        userSettingsRepository.userSettings
            .onEach { settings ->
                if (settings.hasRequestedStepsPermission) {
                    hasRequestedStepsPermission = true
                }
            }.launchIn(viewModelScope)

        listenToWatchActions()

        checkForRunInProgress()
    }

    private fun checkForRunInProgress() {
        if (runningTracker.isTracking.value || runningTracker.elapsedTime.value > Duration.ZERO) {
            state = state.copy(showResumePrompt = true)
            return
        }

        val savedPhase = savedStateHandle.get<String>(KEY_RUN_PHASE)
        if (savedPhase == RunPhase.NotStarted.toSavedStateKey()) {
            return
        }

        viewModelScope.launch {
            val draft = runDraftRepository.getDraft() ?: return@launch
            draftToResume = draft
            state = state.copy(showResumePrompt = true)
        }
    }

    fun onResumeRun() {
        val draft = draftToResume
        if (draft != null) {
            draftToResume = null
            runningTracker.restoreRun(draft)
        }
        updateRunPhase(RunPhase.Paused)
        state = state.copy(showResumePrompt = false)
    }

    fun onDiscardRun() {
        draftToResume = null
        state = state.copy(showResumePrompt = false)
        runningTracker.finishRun()
        updateRunPhase(RunPhase.NotStarted)
    }

    private fun listenToWatchActions() {
        watchConnector.messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.StartOrResume -> {
                        if (state.runPhase != RunPhase.Tracking) {
                            toggleRun(triggeredByWatch = true)
                        }
                    }

                    MessagingAction.Pause -> {
                        if (state.runPhase == RunPhase.Tracking) {
                            toggleRun(triggeredByWatch = true)
                        }
                    }

                    MessagingAction.Finish -> {
                        eventChannel.send(ActiveRunEvent.FinishFromWatch)
                    }

                    else -> {
                        Unit
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun sendActionToWatch(action: MessagingAction) {
        viewModelScope.launch {
            watchConnector.sendActionToWatch(action)
        }
    }

    fun onToggleRun(hasStepsPermission: Boolean) {
        if (state.runPhase != RunPhase.Tracking && !hasStepsPermission && !hasRequestedStepsPermission) {
            requestStepsPermission()
        }
        toggleRun(triggeredByWatch = false)
    }

    private fun requestStepsPermission() {
        hasRequestedStepsPermission = true
        viewModelScope.launch {
            userSettingsRepository.setHasRequestedStepsPermission(true)
            eventChannel.send(ActiveRunEvent.RequestStepsPermission)
        }
    }

    private fun toggleRun(triggeredByWatch: Boolean) {
        when (state.runPhase) {
            RunPhase.Tracking -> {
                runningTracker.setIsTracking(false)
                updateRunPhase(RunPhase.Paused)
                if (!triggeredByWatch) {
                    sendActionToWatch(MessagingAction.Pause)
                }
            }

            RunPhase.NotStarted, RunPhase.Paused -> {
                runningTracker.setIsTracking(true)
                updateRunPhase(RunPhase.Tracking)
                if (!triggeredByWatch) {
                    sendActionToWatch(MessagingAction.StartOrResume)
                }
            }

            else -> {
                Unit
            }
        }
    }

    fun onFinishRun(
        mapPicture: ByteArray,
        triggeredByWatch: Boolean = false,
    ) {
        runningTracker.setIsTracking(false)
        if (!triggeredByWatch) {
            sendActionToWatch(MessagingAction.Finish)
        }

        val runState = state
        val locations = runState.runData.locations
        if (locations.sumOf { it.size } <= 1) {
            viewModelScope.launch {
                runningTracker.finishRun()
                runningTracker.startObservingLocation()
                updateRunPhase(RunPhase.NotStarted)
            }
            return
        }

        updateRunPhase(RunPhase.Saving)

        viewModelScope.launch {
            val weather =
                runState.currentLocation?.let { location ->
                    withTimeoutOrNull(WEATHER_TIMEOUT) {
                        when (val weatherResult = weatherRepository.getCurrentWeather(location)) {
                            is Result.Success -> weatherResult.data
                            is Result.Error -> null
                        }
                    }
                }

            val run =
                Run(
                    id = null,
                    duration = runState.elapsedTime,
                    dateTimeUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")),
                    distanceMeters = runState.runData.distanceMeters,
                    location = runState.currentLocation ?: Location(0.0, 0.0),
                    maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                    totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                    route = locations,
                    weather = weather,
                    steps = runState.runData.steps,
                )

            when (val result = runRepository.upsertRun(run, mapPicture)) {
                is Result.Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.toUiText()))
                    updateRunPhase(RunPhase.Paused)
                }

                is Result.Success -> {
                    runningTracker.finishRun()
                    runningTracker.startObservingLocation()
                    eventChannel.send(ActiveRunEvent.RunSaved)
                    updateRunPhase(RunPhase.NotStarted)
                }
            }
        }
    }

    private fun updateRunPhase(phase: RunPhase) {
        state = state.copy(runPhase = phase)
        savedStateHandle[KEY_RUN_PHASE] = phase.toSavedStateKey()
    }

    private fun RunPhase.toSavedStateKey(): String =
        when (this) {
            RunPhase.NotStarted -> "not_started"
            RunPhase.Tracking -> "tracking"
            RunPhase.Paused -> "paused"
            RunPhase.Saving -> "saving"
            RunPhase.Finished -> "finished"
        }

    override fun onCleared() {
        super.onCleared()
        if (runningTracker.isTracking.value) {
            runningTracker.setIsTracking(false)
        }
        runningTracker.stopObservingLocation()
        activeRunServiceController.stop()
    }

    companion object {
        private const val KEY_RUN_PHASE = "run_phase"
        private val WEATHER_TIMEOUT = 10.seconds
    }
}
