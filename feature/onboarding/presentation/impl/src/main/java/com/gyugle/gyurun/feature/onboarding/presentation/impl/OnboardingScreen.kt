package com.gyugle.gyurun.feature.onboarding.presentation.impl

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.util.lerp
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.MapPinIcon
import com.gyugle.gyurun.core.presentation.designsystem.RunIcon
import com.gyugle.gyurun.core.presentation.designsystem.StatsIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunActionButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunDialog
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.motion
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.ui.hasLocationPermission
import com.gyugle.gyurun.core.presentation.ui.openAppSettings
import com.gyugle.gyurun.core.presentation.ui.shouldShowLocationPermissionRationale
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val locationPermissions =
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

private data class OnboardingPage(
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int,
)

@Composable
internal fun OnboardingScreenRoot(onFinishOnboarding: () -> Unit) {
    val activity = LocalActivity.current as ComponentActivity

    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { _ ->
            when {
                activity.hasLocationPermission() -> onFinishOnboarding()
                activity.shouldShowLocationPermissionRationale() -> showRationaleDialog = true
                else -> showSettingsDialog = true
            }
        }

    OnboardingScreen(
        onGetStartedClick = {
            if (activity.hasLocationPermission()) {
                onFinishOnboarding()
            } else {
                permissionLauncher.launch(locationPermissions)
            }
        },
    )

    if (showRationaleDialog) {
        PermissionDialog(
            title = stringResource(R.string.permission_location_title),
            description = stringResource(R.string.permission_location_rationale),
            confirmText = stringResource(R.string.permission_try_again),
            onConfirm = {
                showRationaleDialog = false
                permissionLauncher.launch(locationPermissions)
            },
            onDismiss = { showRationaleDialog = false },
        )
    }

    if (showSettingsDialog) {
        PermissionDialog(
            title = stringResource(R.string.permission_location_title),
            description = stringResource(R.string.permission_location_settings),
            confirmText = stringResource(R.string.permission_open_settings),
            onConfirm = {
                showSettingsDialog = false
                activity.openAppSettings()
            },
            onDismiss = { showSettingsDialog = false },
        )
    }
}

@Composable
internal fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val runIcon = RunIcon
    val mapPinIcon = MapPinIcon
    val statsIcon = StatsIcon
    val pages =
        remember(runIcon, mapPinIcon, statsIcon) {
            listOf(
                OnboardingPage(
                    runIcon,
                    R.string.onboarding_page_track_title,
                    R.string.onboarding_page_track_description,
                ),
                OnboardingPage(
                    mapPinIcon,
                    R.string.onboarding_page_map_title,
                    R.string.onboarding_page_map_description,
                ),
                OnboardingPage(
                    statsIcon,
                    R.string.onboarding_page_stats_title,
                    R.string.onboarding_page_stats_description,
                ),
            )
        }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val motion = MaterialTheme.motion
    val isLastPage = pagerState.currentPage == pages.lastIndex

    GyuRunScaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    pagerState = pagerState,
                    pageIndex = pageIndex,
                )
            }

            PagerIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.large),
            )

            GyuRunActionButton(
                text = stringResource(if (isLastPage) R.string.onboarding_get_started else R.string.onboarding_next),
                isLoading = false,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.large),
                onClick = {
                    if (isLastPage) {
                        onGetStartedClick()
                    } else {
                        val nextPage = pagerState.currentPage + 1
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = nextPage,
                                animationSpec =
                                    tween(
                                        motion.durationLong,
                                        easing = motion.emphasizedEasing,
                                    ),
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pagerState: PagerState,
    pageIndex: Int,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = MaterialTheme.motion.reduceMotion
    val pageOffset =
        ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue
    val nearness = 1f - pageOffset.coerceIn(0f, 1f)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = lerp(0.4f, 1f, nearness)
                    val scale = if (reduceMotion) 1f else lerp(0.9f, 1f, nearness)
                    scaleX = scale
                    scaleY = scale
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(MaterialTheme.spacing.huge * 2)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(MaterialTheme.spacing.huge),
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        Text(
            text = stringResource(page.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val motion = MaterialTheme.motion
    val indicatorDescription =
        stringResource(R.string.onboarding_page_indicator, currentPage + 1, pageCount)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = indicatorDescription },
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val dotWidth by animateDpAsState(
                targetValue = if (isActive) MaterialTheme.spacing.large else MaterialTheme.spacing.small,
                animationSpec = tween(motion.durationMedium, easing = motion.standardEasing),
                label = "dotWidth",
            )
            val dotColor by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(motion.durationMedium, easing = motion.standardEasing),
                label = "dotColor",
            )
            Box(
                modifier =
                    Modifier
                        .height(MaterialTheme.spacing.small)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor),
            )
        }
    }
}

@Composable
private fun PermissionDialog(
    title: String,
    description: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GyuRunDialog(
        modifier = modifier,
        title = title,
        description = description,
        onDismiss = onDismiss,
        primaryButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
    )
}

@PreviewLightDark
@Composable
private fun OnboardingScreenPreview() {
    GyuRunTheme {
        OnboardingScreen(onGetStartedClick = {})
    }
}

@PreviewLightDark
@Composable
private fun PermissionDialogRationalePreview() {
    GyuRunTheme {
        PermissionDialog(
            title = stringResource(R.string.permission_location_title),
            description = stringResource(R.string.permission_location_rationale),
            confirmText = stringResource(R.string.permission_try_again),
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun PermissionDialogSettingsPreview() {
    GyuRunTheme {
        PermissionDialog(
            title = stringResource(R.string.permission_location_title),
            description = stringResource(R.string.permission_location_settings),
            confirmText = stringResource(R.string.permission_open_settings),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
