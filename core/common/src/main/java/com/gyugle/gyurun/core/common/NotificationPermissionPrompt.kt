package com.gyugle.gyurun.core.common

fun shouldAskNotificationPermission(
    hasAsked: Boolean,
    hasPermission: Boolean,
): Boolean = !hasAsked && !hasPermission
