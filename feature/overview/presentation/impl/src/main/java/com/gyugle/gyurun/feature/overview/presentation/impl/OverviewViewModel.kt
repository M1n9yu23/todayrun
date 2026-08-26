package com.gyugle.gyurun.feature.overview.presentation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gyugle.gyurun.core.common.onError
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.presentation.ui.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class OverviewViewModel(
    private val runRepository: RunRepository,
) : ViewModel() {
    val runs: Flow<PagingData<Run>> =
        runRepository.getRunsPaged().cachedIn(viewModelScope)

    private val eventChannel = Channel<OverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onDeleteRun(run: Run) {
        if (run.id == null) return
        viewModelScope.launch {
            runRepository.deleteRun(run)
            eventChannel.send(OverviewEvent.RunDeleted(run))
        }
    }

    fun onUndoDelete(run: Run) {
        viewModelScope.launch {
            runRepository.restoreRun(run).onError { error ->
                eventChannel.send(OverviewEvent.UndoFailed(error.toUiText()))
            }
        }
    }

    fun onConfirmDelete(run: Run) {
        viewModelScope.launch {
            runRepository.finalizeRunDeletion(run)
        }
    }
}