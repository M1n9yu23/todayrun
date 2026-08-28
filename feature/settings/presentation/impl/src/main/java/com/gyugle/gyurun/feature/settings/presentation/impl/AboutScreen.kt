package com.gyugle.gyurun.feature.settings.presentation.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.map.MapLegalNotice
import com.gyugle.gyurun.core.map.MapProvider
import com.gyugle.gyurun.core.map.activeMapLegalNotice
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.OpenInNewIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunIconButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunToolbar
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import org.koin.compose.koinInject

private const val PRIVACY_POLICY_URL = ""
private const val TERMS_OF_SERVICE_URL = ""

@Composable
internal fun AboutScreenRoot(
    onBackClick: () -> Unit,
    appVersionProvider: AppVersionProvider = koinInject(),
) {
    val context = LocalContext.current
    AboutScreen(
        appVersion = "${appVersionProvider.versionName} (${appVersionProvider.versionCode})",
        libraries = appLibraries,
        mapLegalNotice = activeMapLegalNotice,
        onOpenUrl = context::openUrl,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun AboutScreen(
    appVersion: String,
    libraries: List<OssLibrary>,
    mapLegalNotice: MapLegalNotice,
    onOpenUrl: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GyuRunScaffold(
        modifier = modifier,
        topBar = {
            GyuRunToolbar(
                title = stringResource(R.string.about_title),
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
            AboutHeader(appVersion = appVersion)

            SettingsSection(
                title = stringResource(R.string.about_section_legal),
                contentArrangement = Arrangement.Top,
            ) {
                val mapNoticeTitle = stringResource(R.string.about_map_notice)
                AboutLinkRow(
                    title = mapNoticeTitle,
                    subtitle = mapProviderName(mapLegalNotice.provider),
                    contentDescription = stringResource(R.string.about_open_link_description, mapNoticeTitle),
                    onClick = { onOpenUrl(mapLegalNotice.noticeUrl) },
                )
            }

            SettingsSection(
                title = stringResource(R.string.about_open_source_licenses),
                contentArrangement = Arrangement.Top,
            ) {
                libraries.forEach { library ->
                    AboutLinkRow(
                        title = library.name,
                        subtitle = library.license,
                        contentDescription = stringResource(R.string.about_open_link_description, library.name),
                        onClick = { onOpenUrl(library.url) },
                    )
                }
            }

            SettingsSection(
                title = stringResource(R.string.about_section_policies),
                contentArrangement = Arrangement.Top,
            ) {
                val privacyTitle = stringResource(R.string.about_privacy_policy)
                val termsTitle = stringResource(R.string.about_terms)
                AboutLinkRow(
                    title = privacyTitle,
                    contentDescription = stringResource(R.string.about_open_link_description, privacyTitle),
                    onClick = { onOpenUrl(PRIVACY_POLICY_URL) },
                )
                AboutLinkRow(
                    title = termsTitle,
                    contentDescription = stringResource(R.string.about_open_link_description, termsTitle),
                    onClick = { onOpenUrl(TERMS_OF_SERVICE_URL) },
                )
            }
        }
    }
}

@Composable
private fun mapProviderName(provider: MapProvider): String =
    when (provider) {
        MapProvider.GOOGLE -> stringResource(R.string.settings_map_provider_google)
        MapProvider.KAKAO -> stringResource(R.string.settings_map_provider_kakao)
    }

@Composable
private fun AboutHeader(
    appVersion: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        Text(
            text = stringResource(R.string.about_app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.about_version_label, appVersion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AboutLinkRow(
    title: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    SettingsRow(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
    ) {
        GyuRunIconButton(
            icon = OpenInNewIcon,
            contentDescription = contentDescription,
            onClick = onClick,
        )
    }
}

@PreviewLightDark
@Composable
private fun AboutScreenPreview() {
    GyuRunTheme {
        AboutScreen(
            appVersion = "1.0.0 (1)",
            libraries = appLibraries,
            mapLegalNotice =
                MapLegalNotice(
                    provider = MapProvider.GOOGLE,
                    noticeUrl = "https://www.google.com/help/legalnotices_maps/",
                ),
            onOpenUrl = {},
            onBackClick = {},
        )
    }
}