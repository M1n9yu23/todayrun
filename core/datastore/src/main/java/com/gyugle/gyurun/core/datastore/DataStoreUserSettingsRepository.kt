package com.gyugle.gyurun.core.datastore

import androidx.datastore.core.DataStore
import com.gyugle.gyurun.core.datastore.mapper.toUserSettings
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.preferences.UserSettings
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

internal class DataStoreUserSettingsRepository(
    private val dataStore: DataStore<UserPreferences>,
) : UserSettingsRepository {
    override val userSettings: Flow<UserSettings> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    Timber.e(throwable, "user_prefs 읽기 실패 — 기본값으로 대체")
                    emit(UserPreferences())
                } else {
                    throw throwable
                }
            }.map { it.toUserSettings() }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        dataStore.updateData { it.copy(distanceUnit = unit.name) }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.updateData { it.copy(themeMode = themeMode.name) }
    }

    override suspend fun setHasCompletedOnboarding(hasCompleted: Boolean) {
        dataStore.updateData { it.copy(hasCompletedOnboarding = hasCompleted) }
    }

    override suspend fun setHasRequestedStepsPermission(hasRequested: Boolean) {
        dataStore.updateData { it.copy(hasRequestedStepsPermission = hasRequested) }
    }

    override suspend fun setHasRequestedNotificationPermission(hasRequested: Boolean) {
        dataStore.updateData { it.copy(hasRequestedNotificationPermission = hasRequested) }
    }
}