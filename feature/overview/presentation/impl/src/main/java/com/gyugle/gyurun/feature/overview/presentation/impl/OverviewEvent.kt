package com.gyugle.gyurun.feature.overview.presentation.impl

import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.presentation.ui.UiText

internal sealed interface OverviewEvent {
    data class RunDeleted(
        val run: Run,
    ) : OverviewEvent

    data class UndoFailed(
        val error: UiText,
    ) : OverviewEvent
}