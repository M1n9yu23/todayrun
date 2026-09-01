@file:OptIn(ExperimentalCoroutinesApi::class)

package com.gyugle.gyurun.run.location

import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.restartDelay
import com.gyugle.gyurun.core.connectivity.domain.DeviceNode
import com.gyugle.gyurun.core.connectivity.domain.DeviceType
import com.gyugle.gyurun.core.connectivity.domain.NodeDiscovery
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingClient
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingError
import com.gyugle.gyurun.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber

internal class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient,
) : WatchConnector {
    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice = _connectedDevice.asStateFlow()

    private val isTrackable = MutableStateFlow(false)

    override val messagingActions: Flow<MessagingAction> =
        nodeDiscovery
            .observeConnectedDevices(DeviceType.PHONE)
            .flatMapLatest { connectedDevices ->
                val node = connectedDevices.firstOrNull()
                if (node != null && node.isNearby) {
                    _connectedDevice.value = node
                    messagingClient.connectToNode(node.id)
                } else {
                    _connectedDevice.value = null
                    flowOf()
                }
            }.onEach { action ->
                if (action == MessagingAction.ConnectionRequest) {
                    if (isTrackable.value) {
                        sendActionToWatch(MessagingAction.Trackable)
                    } else {
                        sendActionToWatch(MessagingAction.Untrackable)
                    }
                }
            }.shareIn(applicationScope, SharingStarted.Eagerly)

    init {
        _connectedDevice
            .filterNotNull()
            .flatMapLatest { isTrackable }
            .onEach { isTrackable ->
                val action =
                    if (isTrackable) MessagingAction.Trackable else MessagingAction.Untrackable
                sendActionToWatch(action)
            }.retryWhen { cause, attempt ->
                Timber.w(cause, "워치에게 알리는 통로가 끊겨 다시 잇는다(%d 번째 실패)", attempt + 1)
                delay(restartDelay(attempt))
                true
            }.launchIn(applicationScope)
    }

    override suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError> = messagingClient.sendOrQueueAction(action)

    override fun setIsTrackable(isTrackable: Boolean) {
        this.isTrackable.value = isTrackable
    }
}
