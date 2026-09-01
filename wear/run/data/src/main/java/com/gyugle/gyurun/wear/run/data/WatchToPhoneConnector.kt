package com.gyugle.gyurun.wear.run.data

import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.connectivity.domain.DeviceNode
import com.gyugle.gyurun.core.connectivity.domain.DeviceType
import com.gyugle.gyurun.core.connectivity.domain.NodeDiscovery
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingClient
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingError
import com.gyugle.gyurun.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn

internal class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient,
) : PhoneConnector {
    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedNode = _connectedNode.asStateFlow()

    override val messagingActions =
        nodeDiscovery
            .observeConnectedDevices(DeviceType.WATCH)
            .flatMapLatest { connectedNodes ->
                val node = connectedNodes.firstOrNull()
                if (node != null && node.isNearby) {
                    _connectedNode.value = node
                    messagingClient.connectToNode(node.id)
                } else {
                    _connectedNode.value = null
                    flowOf()
                }
            }.shareIn(applicationScope, SharingStarted.Eagerly)

    override suspend fun sendActionToPhone(action: MessagingAction): EmptyResult<MessagingError> = messagingClient.sendOrQueueAction(action)
}
