package com.gyugle.gyurun.feature.active.presentation.impl

import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.run.domain.RunData
import kotlin.time.Duration

internal data class ActiveRunState(
    val runPhase: RunPhase = RunPhase.NotStarted,
    val elapsedTime: Duration = Duration.ZERO,
    val runData: RunData = RunData(),
    val currentLocation: Location? = null,
    val showResumePrompt: Boolean = false,
)

internal sealed interface RunPhase {
    data object NotStarted : RunPhase

    data object Tracking : RunPhase

    data object Paused : RunPhase

    data object Saving : RunPhase

    data object Finished : RunPhase
}
