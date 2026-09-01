package com.gyugle.gyurun.core.datastore.mapper

import com.gyugle.gyurun.core.datastore.UserPreferences
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.preferences.UserSettings
import com.gyugle.gyurun.core.domain.run.DistanceUnit

internal fun UserPreferences.toUserSettings(): UserSettings =
    UserSettings(
        distanceUnit = parseEnum(distanceUnit, DistanceUnit.KILOMETERS),
        themeMode = parseEnum(themeMode, ThemeMode.SYSTEM),
        hasCompletedOnboarding = hasCompletedOnboarding,
        hasRequestedStepsPermission = hasRequestedStepsPermission,
        hasRequestedNotificationPermission = hasRequestedNotificationPermission,
    )

private inline fun <reified T : Enum<T>> parseEnum(
    name: String,
    default: T,
): T = enumValues<T>().firstOrNull { it.name == name } ?: default
