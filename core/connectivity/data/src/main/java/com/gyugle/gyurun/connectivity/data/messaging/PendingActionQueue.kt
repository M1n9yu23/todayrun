package com.gyugle.gyurun.connectivity.data.messaging

import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

internal class PendingActionQueue(
    private val elapsedRealtimeMillis: () -> Long,
) {
    private val pending =
        Channel<QueuedAction>(
            capacity = MAX_QUEUED_ACTIONS,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    fun enqueue(action: MessagingAction) {
        if (action.isWorthQueueing()) {
            pending.trySend(QueuedAction(action, elapsedRealtimeMillis()))
        }
    }

    fun drain(): List<MessagingAction> {
        val now = elapsedRealtimeMillis()
        val drained = mutableListOf<MessagingAction>()
        while (true) {
            val queued = pending.tryReceive().getOrNull() ?: break
            if (now - queued.queuedAtMillis <= MAX_ACTION_AGE_MILLIS) {
                drained += queued.action
            }
        }
        return drained
    }

    private fun MessagingAction.isWorthQueueing(): Boolean =
        when (this) {
            MessagingAction.StartOrResume,
            MessagingAction.Pause,
            MessagingAction.Finish,
            MessagingAction.ConnectionRequest,
            MessagingAction.Trackable,
            MessagingAction.Untrackable,
                -> true

            is MessagingAction.HeartRateUpdate,
            is MessagingAction.StepCountUpdate,
            is MessagingAction.DistanceUpdate,
            is MessagingAction.TimeUpdate,
                -> false
        }

    private data class QueuedAction(
        val action: MessagingAction,
        val queuedAtMillis: Long,
    )

    companion object {
        private const val MAX_QUEUED_ACTIONS = 20
        private const val MAX_ACTION_AGE_MILLIS = 30_000L
    }
}