package com.gyugle.gyurun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.coroutines.launch

class MainViewModel(
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {
    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            userSettingsRepository.userSettings.collect { settings ->
                state =
                    state.copy(
                        isCheckingOnboarding = false,
                        hasCompletedOnboarding = settings.hasCompletedOnboarding,
                        distanceUnit = settings.distanceUnit,
                        themeMode = settings.themeMode,
                        hasRequestedNotificationPermission =
                            settings.hasRequestedNotificationPermission,
                    )
            }
        }
    }

    fun onNotificationPermissionRequested() {
        viewModelScope.launch {
            userSettingsRepository.setHasRequestedNotificationPermission(true)
        }
    }
}

data class MainState(
    val isCheckingOnboarding: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hasRequestedNotificationPermission: Boolean = true,
)