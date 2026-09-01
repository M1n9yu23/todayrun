package com.gyugle.gyurun.core.domain.preferences

import com.gyugle.gyurun.core.domain.run.DistanceUnit

data class UserSettings(
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hasCompletedOnboarding: Boolean = false,
    val hasRequestedStepsPermission: Boolean = false,
    val hasRequestedNotificationPermission: Boolean = false,
)
