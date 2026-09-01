package com.gyugle.gyurun.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.presentation.ui.UiText
import com.gyugle.gyurun.wear.run.domain.ExerciseTracker
import com.gyugle.gyurun.wear.run.domain.PhoneConnector
import com.gyugle.gyurun.wear.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker,
) : ViewModel() {
    var state by mutableStateOf(TrackerState())
        private set

    private val eventChannel = Channel<TrackerEvent>()
    val events = eventChannel.receiveAsFlow()

    private val isTracking =
        snapshotFlow {
            state.isRunActive && state.isTrackable && state.isConnectedPhoneNearby
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        phoneConnector.connectedNode
            .onEach { node ->
                state = state.copy(isConnectedPhoneNearby = node?.isNearby == true)
            }.combine(isTracking) { node, isTracking ->
                if (node != null && !isTracking) {
                    phoneConnector.sendActionToPhone(MessagingAction.ConnectionRequest)
                }
            }.launchIn(viewModelScope)

        runningTracker.isTrackable
            .onEach { isTrackable ->
                state = state.copy(isTrackable = isTrackable)
            }.launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                if (isTracking) {
                    val result =
                        if (state.hasStartedRunning) {
                            exerciseTracker.resumeExercise()
                        } else {
                            exerciseTracker.startExercise()
                        }
                    if (result is Result.Error) {
                        eventChannel.send(
                            TrackerEvent.Error(UiText.StringResource(R.string.wear_exercise_error)),
                        )
                    } else {
                        state = state.copy(hasStartedRunning = true)
                    }
                } else if (state.hasStartedRunning) {
                    if (exerciseTracker.pauseExercise() is Result.Error) {
                        eventChannel.send(
                            TrackerEvent.Error(UiText.StringResource(R.string.wear_exercise_pause_error)),
                        )
                    }
                }
                runningTracker.setIsTracking(isTracking)
            }.launchIn(viewModelScope)

        runningTracker.exerciseServiceFailures
            .onEach {
                state = state.copy(isRunActive = false)
                eventChannel.send(
                    TrackerEvent.Error(UiText.StringResource(R.string.wear_exercise_service_error)),
                )
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            state = state.copy(canTrackHeartRate = exerciseTracker.isHeartRateTrackingSupported())
        }

        exerciseTracker.isHeartRateAvailable
            .onEach { isAvailable ->
                state = state.copy(isHeartRateAvailable = isAvailable)
            }.launchIn(viewModelScope)

        runningTracker.heartRate
            .onEach { heartRate ->
                state = state.copy(heartRate = heartRate)
            }.launchIn(viewModelScope)

        runningTracker.elapsedTime
            .onEach { elapsedDuration ->
                state = state.copy(elapsedDuration = elapsedDuration)
            }.launchIn(viewModelScope)

        runningTracker.distanceMeters
            .onEach { distanceMeters ->
                state = state.copy(distanceMeters = distanceMeters)
            }.launchIn(viewModelScope)

        listenToPhoneActions()
    }

    fun onAction(action: TrackerAction) {
        onAction(action, triggeredOnPhone = false)
    }

    private fun onAction(
        action: TrackerAction,
        triggeredOnPhone: Boolean,
    ) {
        if (!triggeredOnPhone) {
            sendActionToPhone(action)
        }
        when (action) {
            TrackerAction.OnFinishRunClick -> {
                viewModelScope.launch {
                    if (exerciseTracker.stopExercise() is Result.Error) {
                        eventChannel.send(
                            TrackerEvent.Error(UiText.StringResource(R.string.wear_exercise_stop_error)),
                        )
                    }
                    eventChannel.send(TrackerEvent.RunFinished)
                    state =
                        state.copy(
                            elapsedDuration = Duration.ZERO,
                            distanceMeters = 0,
                            heartRate = 0,
                            hasStartedRunning = false,
                            isRunActive = false,
                        )
                }
            }

            TrackerAction.OnToggleRunClick -> {
                if (state.isTrackable) {
                    state = state.copy(isRunActive = !state.isRunActive)
                }
            }

            TrackerAction.OnExercisePermissionClick -> {
                state = state.copy(isRequestingPermission = true)
            }

            is TrackerAction.OnPermissionResult -> {
                viewModelScope.launch {
                    state =
                        state.copy(
                            activityPermission = action.activityPermission,
                            heartRatePermission = action.heartRatePermission,
                            isRequestingPermission = false,
                            canTrackHeartRate = exerciseTracker.isHeartRateTrackingSupported(),
                        )
                }
            }
        }
    }

    private fun sendActionToPhone(action: TrackerAction) {
        viewModelScope.launch {
            val messagingAction =
                when (action) {
                    TrackerAction.OnFinishRunClick -> {
                        MessagingAction.Finish
                    }

                    TrackerAction.OnToggleRunClick -> {
                        if (state.isRunActive) MessagingAction.Pause else MessagingAction.StartOrResume
                    }

                    else -> {
                        null
                    }
                }
            messagingAction?.let { phoneConnector.sendActionToPhone(it) }
        }
    }

    private fun listenToPhoneActions() {
        phoneConnector.messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.Finish -> {
                        onAction(
                            TrackerAction.OnFinishRunClick,
                            triggeredOnPhone = true,
                        )
                    }

                    MessagingAction.Pause -> {
                        if (state.isTrackable) {
                            state =
                                state.copy(isRunActive = false)
                        }
                    }

                    MessagingAction.StartOrResume -> {
                        if (state.isTrackable) {
                            state =
                                state.copy(isRunActive = true)
                        }
                    }

                    else -> {
                        Unit
                    }
                }
            }.launchIn(viewModelScope)
    }
}
