package com.gyugle.gyurun.core.domain.preferences.repository

import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.preferences.UserSettings
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val userSettings: Flow<UserSettings>

    suspend fun setDistanceUnit(unit: DistanceUnit)

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setHasCompletedOnboarding(hasCompleted: Boolean)

    suspend fun setHasRequestedStepsPermission(hasRequested: Boolean)

    suspend fun setHasRequestedNotificationPermission(hasRequested: Boolean)
}
