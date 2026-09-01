package com.gyugle.gyurun.connectivity.data

import com.gyugle.gyurun.core.connectivity.domain.DeviceType

internal const val CAPABILITY_PHONE_APP = "gyurun_phone_app"
internal const val CAPABILITY_WEAR_APP = "gyurun_wear_app"

internal fun remoteCapabilityFor(localDeviceType: DeviceType): String =
    when (localDeviceType) {
        DeviceType.PHONE -> CAPABILITY_WEAR_APP
        DeviceType.WATCH -> CAPABILITY_PHONE_APP
    }
