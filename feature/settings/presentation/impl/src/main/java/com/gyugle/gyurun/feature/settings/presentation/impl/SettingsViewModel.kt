package com.gyugle.gyurun.feature.settings.presentation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {
    val state: StateFlow<SettingsState> =
        userSettingsRepository.userSettings
            .map { settings ->
                SettingsState(
                    distanceUnit = settings.distanceUnit,
                    themeMode = settings.themeMode,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsState(),
            )

    fun onSelectDistanceUnit(unit: DistanceUnit) {
        viewModelScope.launch {
            userSettingsRepository.setDistanceUnit(unit)
        }
    }

    fun onSelectThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userSettingsRepository.setThemeMode(themeMode)
        }
    }
}

internal data class SettingsState(
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
