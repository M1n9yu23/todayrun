package com.gyugle.gyurun.connectivity.data.messaging

import android.content.Context
import android.os.SystemClock
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingClient
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

internal class WearMessagingClient(
    context: Context,
) : MessagingClient {
    private val messageClient = Wearable.getMessageClient(context)

    private val pendingActions = PendingActionQueue(SystemClock::elapsedRealtime)

    @Volatile
    private var connectedNodeId: String? = null

    override fun connectToNode(nodeId: String): Flow<MessagingAction> {
        connectedNodeId = nodeId

        return callbackFlow {
            val listener: (MessageEvent) -> Unit = { event ->
                if (event.path.startsWith(MESSAGING_ACTION_PATH)) {
                    decodeMessagingAction(event.data.decodeToString())?.let { trySend(it) }
                }
            }

            messageClient.addListener(listener)

            pendingActions.drain().forEach { sendOrQueueAction(it) }

            awaitClose {
                messageClient.removeListener(listener)
            }
        }
    }

    override suspend fun sendOrQueueAction(action: MessagingAction): EmptyResult<MessagingError> =
        connectedNodeId?.let { nodeId ->
            try {
                val json = encodeMessagingAction(action)
                messageClient
                    .sendMessage(nodeId, MESSAGING_ACTION_PATH, json.encodeToByteArray())
                    .await()
                Result.Success(Unit)
            } catch (e: ApiException) {
                Timber.w(e, "액션을 보내지 못했다")
                Result.Error(
                    if (e.status.isInterrupted) {
                        MessagingError.CONNECTION_INTERRUPTED
                    } else {
                        MessagingError.UNKNOWN
                    },
                )
            }
        } ?: run {
            pendingActions.enqueue(action)
            Result.Error(MessagingError.DISCONNECTED)
        }

    companion object {
        private const val MESSAGING_ACTION_PATH = "gyurun/messaging_action"
    }
}
