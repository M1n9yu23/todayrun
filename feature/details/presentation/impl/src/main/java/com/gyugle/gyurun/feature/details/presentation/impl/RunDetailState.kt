package com.gyugle.gyurun.feature.details.presentation.impl

import com.gyugle.gyurun.core.domain.run.Run

internal sealed interface RunDetailState {
    data object Loading : RunDetailState

    data object NotFound : RunDetailState

    data class Content(
        val run: Run,
    ) : RunDetailState
}