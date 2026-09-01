package com.gyugle.gyurun.core.datastore

import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.serialization.Serializable

@Serializable
internal data class UserPreferences(
    val distanceUnit: String = DistanceUnit.KILOMETERS.name,
    val themeMode: String = ThemeMode.SYSTEM.name,
    val hasCompletedOnboarding: Boolean = false,
    val hasRequestedStepsPermission: Boolean = false,
    val hasRequestedNotificationPermission: Boolean = false,
)
