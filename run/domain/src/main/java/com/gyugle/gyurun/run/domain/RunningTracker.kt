@file:OptIn(ExperimentalCoroutinesApi::class)

package com.gyugle.gyurun.run.domain

import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import com.gyugle.gyurun.core.domain.run.RunDraft
import com.gyugle.gyurun.core.domain.run.repository.RunDraftRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val stepObserver: StepObserver,
    private val applicationScope: CoroutineScope,
    private val watchConnector: WatchConnector,
    private val runDraftRepository: RunDraftRepository,
) {
    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private var completedSteps = 0

    private var runStartTime: ZonedDateTime? = null

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation =
        isObservingLocation
            .flatMapLatest { isObservingLocation ->
                if (isObservingLocation) {
                    locationObserver.observeLocation(1000L)
                } else {
                    flowOf(null)
                }
            }.stateIn(
                applicationScope,
                SharingStarted.Lazily,
                null,
            )

    init {
        _isTracking
            .onEach { isTracking ->
                if (!isTracking) {
                    val newList =
                        buildList {
                            addAll(runData.value.locations)
                            add(emptyList<LocationTimestamp>())
                        }
                    _runData.update { it.copy(locations = newList) }
                }
            }.flatMapLatest { isTracking ->
                if (isTracking) {
                    Timer.timeAndEmit()
                } else {
                    flowOf()
                }
            }.onEach { interval ->
                _elapsedTime.value += interval
            }.launchIn(applicationScope)

        currentLocation
            .combineTransform(_isTracking) { location, isTracking ->
                if (isTracking && location != null) {
                    emit(
                        LocationTimestamp(
                            location = location,
                            durationTimestamp = _elapsedTime.value,
                        ),
                    )
                }
            }.onEach { locationTimestamp ->
                val currentLocations = runData.value.locations
                val lastLocationsList =
                    if (currentLocations.isNotEmpty()) {
                        currentLocations.last() + locationTimestamp
                    } else {
                        listOf(locationTimestamp)
                    }
                val newLocationsList = currentLocations.replaceLast(lastLocationsList)

                val distanceMeters =
                    LocationDataCalculator.getTotalDistanceMeters(
                        locations = newLocationsList,
                    )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = locationTimestamp.durationTimestamp

                val avgSecondsPerKm =
                    if (distanceKm == 0.0) {
                        0
                    } else {
                        (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                    }

                _runData.update {
                    it.copy(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationsList,
                    )
                }
            }.launchIn(applicationScope)

        _isTracking
            .onEach { isTracking ->
                if (!isTracking) {
                    completedSteps = runData.value.steps ?: 0
                }
            }.flatMapLatest { isTracking ->
                if (isTracking) {
                    stepObserver.observeSteps()
                } else {
                    flowOf()
                }
            }.onEach { sessionSteps ->
                _runData.update { it.copy(steps = completedSteps + sessionSteps) }
            }.launchIn(applicationScope)

        _isTracking
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    flow {
                        while (true) {
                            emit(Unit)
                            delay(DRAFT_SAVE_INTERVAL)
                        }
                    }
                } else {
                    flowOf()
                }
            }.onEach {
                saveDraft()
            }.launchIn(applicationScope)

        locationObserver.isLocationAvailable
            .onEach { isAvailable ->
                _runData.update { it.copy(isLocationAvailable = isAvailable) }
            }.launchIn(applicationScope)

        _isTracking
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    watchConnector.messagingActions
                } else {
                    flowOf()
                }
            }.filterIsInstance<MessagingAction.HeartRateUpdate>()
            .onEach { update ->
                _runData.update { it.copy(heartRate = update.heartRate) }
            }.launchIn(applicationScope)

        elapsedTime
            .onEach { elapsedTime ->
                watchConnector.sendActionToWatch(MessagingAction.TimeUpdate(elapsedTime))
            }.launchIn(applicationScope)

        runData
            .map { it.distanceMeters }
            .distinctUntilChanged()
            .onEach { distanceMeters ->
                watchConnector.sendActionToWatch(MessagingAction.DistanceUpdate(distanceMeters))
            }.launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        if (isTracking && runStartTime == null) {
            runStartTime = ZonedDateTime.now(ZoneId.of("UTC"))
        }
        _isTracking.value = isTracking
    }

    fun restoreRun(draft: RunDraft) {
        runStartTime = draft.dateTimeUtc
        completedSteps = draft.steps ?: 0
        _elapsedTime.value = draft.duration

        val distanceKm = draft.distanceMeters / 1000.0
        val pace =
            if (distanceKm == 0.0) {
                Duration.ZERO
            } else {
                (draft.duration.inWholeSeconds / distanceKm).roundToInt().seconds
            }

        _runData.value =
            RunData(
                distanceMeters = draft.distanceMeters,
                pace = pace,
                locations = draft.route,
                steps = draft.steps,
            )
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
        watchConnector.setIsTrackable(true)
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
        watchConnector.setIsTrackable(false)
    }

    fun finishRun() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        completedSteps = 0
        runStartTime = null
        _runData.value = RunData()
        applicationScope.launch {
            runDraftRepository.deleteDraft()
        }
    }

    private suspend fun saveDraft() {
        val startTime = runStartTime ?: return
        val currentRunData = _runData.value
        runDraftRepository.upsertDraft(
            RunDraft(
                dateTimeUtc = startTime,
                duration = _elapsedTime.value,
                distanceMeters = currentRunData.distanceMeters,
                route = currentRunData.locations,
                steps = currentRunData.steps,
            ),
        )
    }
}

private val DRAFT_SAVE_INTERVAL = 5.seconds

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if (this.isEmpty()) {
        return listOf(replacement)
    }
    return this.dropLast(1) + listOf(replacement)
}
