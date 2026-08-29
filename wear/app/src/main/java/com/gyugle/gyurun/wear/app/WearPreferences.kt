package com.gyugle.gyurun.wear.app

import kotlinx.serialization.Serializable

@Serializable
internal data class WearPreferences(
    val hasAskedNotificationPermission: Boolean = false,
)