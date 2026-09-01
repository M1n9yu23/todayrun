package com.gyugle.gyurun.wear.run.domain

import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.connectivity.domain.DeviceNode
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {
    val connectedNode: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToPhone(action: MessagingAction): EmptyResult<MessagingError>
}
