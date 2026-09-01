package com.gyugle.gyurun.wear.run.domain

import com.gyugle.gyurun.core.common.restartDelay
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class RunningTracker(
    private val phoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope,
    private val restartLogger: RestartLogger,
) {
    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    private val _isExerciseServiceRunning = MutableStateFlow(false)
    val isExerciseServiceRunning = _isExerciseServiceRunning.asStateFlow()

    private val _exerciseServiceFailures =
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val exerciseServiceFailures = _exerciseServiceFailures.asSharedFlow()

    val distanceMeters =
        phoneConnector.messagingActions
            .filterIsInstance<MessagingAction.DistanceUpdate>()
            .map { it.distanceMeters }
            .stateIn(applicationScope, SharingStarted.Lazily, 0)

    val elapsedTime =
        phoneConnector.messagingActions
            .filterIsInstance<MessagingAction.TimeUpdate>()
            .map { it.elapsedDuration }
            .stateIn(applicationScope, SharingStarted.Lazily, Duration.ZERO)

    init {
        phoneConnector.messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.Trackable -> _isTrackable.value = true
                    MessagingAction.Untrackable -> _isTrackable.value = false
                    else -> Unit
                }
            }.restartOnFailure("폰이 보내는 말")
            .launchIn(applicationScope)

        phoneConnector.connectedNode
            .filterNotNull()
            .onEach { exerciseTracker.prepareExercise() }
            .restartOnFailure("운동 준비")
            .launchIn(applicationScope)

        _isTracking
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    exerciseTracker.heartRate
                } else {
                    flowOf()
                }
            }.onEach { heartRate ->
                phoneConnector.sendActionToPhone(MessagingAction.HeartRateUpdate(heartRate))
                _heartRate.value = heartRate
            }.restartOnFailure("심박수")
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }

    fun setExerciseServiceRunning(isRunning: Boolean) {
        _isExerciseServiceRunning.value = isRunning
    }

    fun reportExerciseServiceFailed() {
        _exerciseServiceFailures.tryEmit(Unit)
    }

    private fun <T> Flow<T>.restartOnFailure(pipe: String): Flow<T> =
        retryWhen { cause, attempt ->
            restartLogger.onRestart(pipe, cause, attempt)
            delay(restartDelay(attempt))
            true
        }
}
