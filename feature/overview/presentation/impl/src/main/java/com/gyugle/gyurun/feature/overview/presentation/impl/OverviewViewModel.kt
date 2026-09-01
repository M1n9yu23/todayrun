package com.gyugle.gyurun.feature.overview.presentation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gyugle.gyurun.core.common.onError
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.presentation.ui.toUiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class OverviewViewModel(
    private val runRepository: RunRepository,
    private val applicationScope: CoroutineScope,
) : ViewModel() {
    val runs: Flow<PagingData<Run>> =
        runRepository.getRunsPaged().cachedIn(viewModelScope)

    private val eventChannel = Channel<OverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    private val pendingDeletions = linkedMapOf<RunId, Run>()

    fun onDeleteRun(run: Run) {
        val id = run.id ?: return
        viewModelScope.launch {
            runRepository.deleteRun(run)
            pendingDeletions[id] = run
            eventChannel.send(OverviewEvent.RunDeleted(run))
        }
    }

    fun onUndoDelete(run: Run) {
        val id = run.id ?: return
        pendingDeletions.remove(id)
        viewModelScope.launch {
            runRepository.restoreRun(run).onError { error ->
                eventChannel.send(OverviewEvent.UndoFailed(error.toUiText()))
            }
        }
    }

    fun onConfirmDelete(run: Run) {
        val id = run.id ?: return
        val pending = pendingDeletions.remove(id) ?: return
        applicationScope.launch {
            runRepository.finalizeRunDeletion(pending)
        }
    }

    override fun onCleared() {
        super.onCleared()
        val abandoned = pendingDeletions.values.toList()
        pendingDeletions.clear()
        if (abandoned.isEmpty()) return
        applicationScope.launch {
            abandoned.forEach { runRepository.finalizeRunDeletion(it) }
        }
    }
}
