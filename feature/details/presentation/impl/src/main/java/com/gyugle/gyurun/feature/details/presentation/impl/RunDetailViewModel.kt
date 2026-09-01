package com.gyugle.gyurun.feature.details.presentation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class RunDetailViewModel(
    runId: String,
    runRepository: RunRepository,
) : ViewModel() {
    val state: StateFlow<RunDetailState> =
        runRepository
            .getRun(RunId(runId))
            .map { run ->
                if (run == null) {
                    RunDetailState.NotFound
                } else {
                    RunDetailState.Content(run)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RunDetailState.Loading,
            )
}
