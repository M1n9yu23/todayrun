package com.gyugle.gyurun.wear.run.presentation

import com.gyugle.gyurun.core.presentation.ui.UiText

sealed interface TrackerEvent {
    data object RunFinished : TrackerEvent

    data class Error(
        val message: UiText,
    ) : TrackerEvent
}
