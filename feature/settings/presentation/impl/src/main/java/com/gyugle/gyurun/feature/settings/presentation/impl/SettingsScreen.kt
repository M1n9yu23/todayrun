package com.gyugle.gyurun.feature.settings.presentation.impl

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import com.gyugle.gyurun.core.map.MapProvider
import com.gyugle.gyurun.core.map.activeMapProvider
import com.gyugle.gyurun.core.presentation.designsystem.ChevronRightIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunSegmentedButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunToolbar
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreenRoot(
    onBackClick: () -> Unit,
    onOpenAbout: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        mapProvider = activeMapProvider,
        selectedLanguage = currentAppLanguage(),
        onSelectDistanceUnit = viewModel::onSelectDistanceUnit,
        onSelectThemeMode = viewModel::onSelectThemeMode,
        onSelectLanguage = { language ->
            AppCompatDelegate.setApplicationLocales(language.toLocaleList())
        },
        onOpenAbout = onOpenAbout,
        onBackClick = onBackClick,
    )
}

private fun currentAppLanguage(): AppLanguage = appLanguageFromTags(AppCompatDelegate.getApplicationLocales().toLanguageTags())

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    mapProvider: MapProvider,
    selectedLanguage: AppLanguage,
    onSelectDistanceUnit: (DistanceUnit) -> Unit,
    onSelectThemeMode: (ThemeMode) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onOpenAbout: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GyuRunScaffold(
        modifier = modifier,
        topBar = {
            GyuRunToolbar(
                title = stringResource(R.string.settings_title),
                showBackButton = true,
                onBackClick = onBackClick,
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        ) {
            SettingsChoiceSection(
                title = stringResource(R.string.settings_section_units),
                options = listOf(DistanceUnit.KILOMETERS, DistanceUnit.MILES),
                selected = state.distanceUnit,
                optionLabel = { unit ->
                    when (unit) {
                        DistanceUnit.KILOMETERS -> stringResource(R.string.settings_units_km)
                        DistanceUnit.MILES -> stringResource(R.string.settings_units_mi)
                    }
                },
                onSelect = onSelectDistanceUnit,
            )

            SettingsChoiceSection(
                title = stringResource(R.string.settings_section_theme),
                options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK),
                selected = state.themeMode,
                optionLabel = { mode ->
                    when (mode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
                        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
                        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
                    }
                },
                onSelect = onSelectThemeMode,
            )

            SettingsChoiceSection(
                title = stringResource(R.string.settings_section_language),
                options = listOf(AppLanguage.SYSTEM, AppLanguage.ENGLISH, AppLanguage.KOREAN),
                selected = selectedLanguage,
                optionLabel = { language ->
                    when (language) {
                        AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
                        AppLanguage.ENGLISH -> stringResource(R.string.settings_language_english)
                        AppLanguage.KOREAN -> stringResource(R.string.settings_language_korean)
                    }
                },
                onSelect = onSelectLanguage,
            )

            SettingsSection(title = stringResource(R.string.settings_section_map)) {
                val providerName =
                    when (mapProvider) {
                        MapProvider.GOOGLE -> stringResource(R.string.settings_map_provider_google)
                        MapProvider.KAKAO -> stringResource(R.string.settings_map_provider_kakao)
                    }
                SettingsRow(
                    title = stringResource(R.string.settings_map_provider),
                    subtitle = stringResource(R.string.settings_map_provider_caption),
                ) {
                    Text(
                        text = providerName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.settings_section_about)) {
                SettingsRow(
                    modifier =
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onOpenAbout,
                        ),
                    title = stringResource(R.string.settings_about),
                    subtitle = stringResource(R.string.settings_about_caption),
                ) {
                    Icon(
                        imageVector = ChevronRightIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> SettingsChoiceSection(
    title: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val optionLabels = options.associateWith { optionLabel(it) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { heading() },
        )
        GyuRunSegmentedButton(
            options = options,
            selectedOption = selected,
            onOptionSelect = onSelect,
            optionLabel = { optionLabels.getValue(it) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    GyuRunTheme {
        SettingsScreen(
            state =
                SettingsState(
                    distanceUnit = DistanceUnit.KILOMETERS,
                    themeMode = ThemeMode.SYSTEM,
                ),
            mapProvider = MapProvider.GOOGLE,
            selectedLanguage = AppLanguage.SYSTEM,
            onSelectDistanceUnit = {},
            onSelectThemeMode = {},
            onSelectLanguage = {},
            onOpenAbout = {},
            onBackClick = {},
        )
    }
}
