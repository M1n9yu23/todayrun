package com.gyugle.gyurun.connectivity.data

import com.google.android.gms.wearable.Node
import com.gyugle.gyurun.core.connectivity.domain.DeviceNode

internal fun Node.toDeviceNode(): DeviceNode =
    DeviceNode(
        id = id,
        displayName = displayName,
        isNearby = isNearby,
    )
