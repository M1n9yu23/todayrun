package com.gyugle.gyurun.feature.active.presentation.impl

import com.gyugle.gyurun.core.presentation.ui.UiText

internal sealed interface ActiveRunEvent {
    data class Error(
        val error: UiText,
    ) : ActiveRunEvent

    data object RunSaved : ActiveRunEvent

    data object FinishFromWatch : ActiveRunEvent

    data object RequestStepsPermission : ActiveRunEvent
}