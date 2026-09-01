package com.gyugle.gyurun.core.connectivity.domain.messaging

import com.gyugle.gyurun.core.common.EmptyResult
import kotlinx.coroutines.flow.Flow

interface MessagingClient {
    fun connectToNode(nodeId: String): Flow<MessagingAction>

    suspend fun sendOrQueueAction(action: MessagingAction): EmptyResult<MessagingError>
}
