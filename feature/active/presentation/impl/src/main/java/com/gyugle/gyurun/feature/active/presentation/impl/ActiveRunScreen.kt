package com.gyugle.gyurun.feature.active.presentation.impl

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.map.MapView
import com.gyugle.gyurun.core.map.RunMapState
import com.gyugle.gyurun.core.map.renderRouteThumbnail
import com.gyugle.gyurun.core.presentation.designsystem.ArrowBackIcon
import com.gyugle.gyurun.core.presentation.designsystem.ErrorIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.PauseIcon
import com.gyugle.gyurun.core.presentation.designsystem.PlayIcon
import com.gyugle.gyurun.core.presentation.designsystem.RunIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunActionButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunCard
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunDialog
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunFilledIconButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunOutlinedActionButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.StatTile
import com.gyugle.gyurun.core.presentation.designsystem.motion
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.designsystem.statLarge
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.ObserveAsEvents
import com.gyugle.gyurun.core.presentation.ui.canTrackLocation
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatPace
import com.gyugle.gyurun.core.presentation.ui.formatSteps
import com.gyugle.gyurun.core.presentation.ui.hasActivityRecognitionPermission
import com.gyugle.gyurun.core.presentation.ui.openAppSettings
import com.gyugle.gyurun.core.presentation.ui.shouldShowActivityRecognitionPermissionRationale
import com.gyugle.gyurun.core.presentation.ui.shouldShowLocationPermissionRationale
import com.gyugle.gyurun.run.domain.RunData
import com.gyugle.gyurun.run.location.ActiveRunService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ActiveRunScreenRoot(
    onExit: () -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as? ComponentActivity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissions =
        remember {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    var showLocationRationale by rememberSaveable { mutableStateOf(false) }
    var showLocationSettings by rememberSaveable { mutableStateOf(false) }
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { _ ->
            when {
                context.canTrackLocation() -> {
                    viewModel.onToggleRun(context.hasActivityRecognitionPermission())
                }

                activity?.shouldShowLocationPermissionRationale() == true -> {
                    showLocationRationale = true
                }

                else -> {
                    showLocationSettings = true
                }
            }
        }

    val stepsPermissionMessage = stringResource(R.string.active_run_steps_permission_rationale)
    val stepsRetryLabel = stringResource(R.string.active_run_enable)

    var showStepsRationale by rememberSaveable { mutableStateOf(false) }
    val activityRecognitionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (!isGranted && activity?.shouldShowActivityRecognitionPermissionRationale() == true) {
                showStepsRationale = true
            }
        }

    LaunchedEffect(showStepsRationale) {
        if (showStepsRationale) {
            showStepsRationale = false
            val result =
                snackbarHostState.showSnackbar(
                    message = stepsPermissionMessage,
                    actionLabel = stepsRetryLabel,
                    duration = SnackbarDuration.Long,
                )
            if (result == SnackbarResult.ActionPerformed) {
                activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    val retryLabel = stringResource(R.string.active_run_retry)

    val performFinish: (Boolean) -> Unit = { triggeredByWatch ->
        scope.launch {
            val route =
                viewModel.state.runData.locations.map { segment ->
                    segment.map { it.location.location }
                }
            val mapPicture =
                withContext(Dispatchers.Default) {
                    renderRouteThumbnail(route)
                }
            viewModel.onFinishRun(mapPicture, triggeredByWatch)
        }
    }
    val onFinishRun: () -> Unit = { performFinish(false) }

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is ActiveRunEvent.Error -> {
                scope.launch {
                    val result =
                        snackbarHostState.showSnackbar(
                            message = event.error.asString(context),
                            actionLabel = retryLabel,
                            duration = SnackbarDuration.Long,
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        onFinishRun()
                    }
                }
            }

            ActiveRunEvent.RunSaved -> {
                onExit()
            }

            ActiveRunEvent.FinishFromWatch -> {
                performFinish(true)
            }

            ActiveRunEvent.RequestStepsPermission -> {
                activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    val onToggleRun: () -> Unit = {
        val willStartTracking = viewModel.state.runPhase != RunPhase.Tracking
        if (willStartTracking && !context.canTrackLocation()) {
            locationPermissionLauncher.launch(locationPermissions)
        } else {
            viewModel.onToggleRun(context.hasActivityRecognitionPermission())
        }
    }

    ActiveRunScreen(
        state = viewModel.state,
        snackbarHostState = snackbarHostState,
        onToggleRun = onToggleRun,
        onFinishRun = onFinishRun,
        onExit = onExit,
    )

    if (showLocationRationale) {
        GyuRunDialog(
            title = stringResource(R.string.active_run_location_permission_title),
            description = stringResource(R.string.active_run_location_rationale),
            onDismiss = { showLocationRationale = false },
            primaryButton = {
                TextButton(
                    onClick = {
                        showLocationRationale = false
                        locationPermissionLauncher.launch(locationPermissions)
                    },
                ) {
                    Text(text = stringResource(R.string.active_run_enable))
                }
            },
        )
    }

    if (showLocationSettings) {
        GyuRunDialog(
            title = stringResource(R.string.active_run_location_permission_title),
            description = stringResource(R.string.active_run_location_settings),
            onDismiss = { showLocationSettings = false },
            primaryButton = {
                TextButton(
                    onClick = {
                        showLocationSettings = false
                        context.openAppSettings()
                    },
                ) {
                    Text(text = stringResource(R.string.active_run_open_settings))
                }
            },
        )
    }

    if (viewModel.state.showResumePrompt) {
        ResumeRunDialog(
            onResume = viewModel::onResumeRun,
            onDiscard = viewModel::onDiscardRun,
        )
    }
}

@Composable
internal fun ResumeRunDialog(
    onResume: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GyuRunDialog(
        modifier = modifier,
        title = stringResource(R.string.active_run_resume_title),
        description = stringResource(R.string.active_run_resume_description),
        icon = RunIcon,
        onDismiss = {},
        primaryButton = {
            TextButton(onClick = onResume) {
                Text(text = stringResource(R.string.active_run_resume_confirm))
            }
        },
        secondaryButton = {
            TextButton(onClick = onDiscard) {
                Text(
                    text = stringResource(R.string.active_run_discard),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    snackbarHostState: SnackbarHostState,
    onToggleRun: () -> Unit,
    onFinishRun: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val motion = MaterialTheme.motion

    LaunchedEffect(state.runPhase, activity) {
        val host = activity ?: return@LaunchedEffect
        when (state.runPhase) {
            RunPhase.Tracking, RunPhase.Paused -> {
                if (!ActiveRunService.isServiceActive && context.canTrackLocation()) {
                    context.startService(
                        ActiveRunService.createStartIntent(context, host::class.java),
                    )
                }
            }

            RunPhase.NotStarted, RunPhase.Saving, RunPhase.Finished -> {
                if (ActiveRunService.isServiceActive) {
                    context.startService(ActiveRunService.createStopIntent(context))
                }
            }
        }
    }

    val view = LocalView.current
    val keepScreenOn = state.runPhase == RunPhase.Tracking || state.runPhase == RunPhase.Paused
    DisposableEffect(keepScreenOn) {
        view.keepScreenOn = keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    val mapState =
        RunMapState(
            currentLocation = state.currentLocation,
            locations =
                state.runData.locations.map { segment ->
                    segment.map { it.location.location }
                },
        )

    val signalLost = isSignalLost(state.runData.isLocationAvailable, state.runPhase)

    GyuRunScaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            MapView(
                state = mapState,
                modifier = Modifier.fillMaxSize(),
            )

            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(MaterialTheme.spacing.medium)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                GyuRunFilledIconButton(
                    icon = ArrowBackIcon,
                    contentDescription = stringResource(R.string.active_run_exit),
                    onClick = onExit,
                )

                RunStatsCard(
                    runData = state.runData,
                    elapsedTime = state.elapsedTime,
                    isTracking = state.runPhase == RunPhase.Tracking,
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility(
                    visible = signalLost,
                    enter = motion.contentEnter,
                    exit = motion.contentExit,
                ) {
                    SignalLostBanner(modifier = Modifier.fillMaxWidth())
                }
            }

            RunControls(
                runPhase = state.runPhase,
                onToggleRun = onToggleRun,
                onFinishRun = onFinishRun,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(MaterialTheme.spacing.large)
                        .fillMaxWidth(),
            )

            AnimatedVisibility(
                visible = state.runPhase == RunPhase.Saving,
                enter = fadeIn(tween(motion.durationMedium, easing = motion.standardEasing)),
                exit = motion.contentExit,
            ) {
                SavingOverlay(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
internal fun RunStatsCard(
    runData: RunData,
    elapsedTime: Duration,
    isTracking: Boolean,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    GyuRunCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val elapsed = formatElapsedTime(elapsedTime)
            val elapsedLabel = stringResource(R.string.active_run_elapsed_time, elapsed)
            Row(
                modifier = Modifier.clearAndSetSemantics { contentDescription = elapsedLabel },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                RecordingIndicator(isActive = isTracking)
                Text(
                    text = elapsed,
                    style = MaterialTheme.typography.statLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                StatTile(
                    value = formatDistance(runData.distanceMeters, unit),
                    label = stringResource(R.string.active_run_stat_distance),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = formatPace(runData.pace, unit),
                    label = stringResource(R.string.active_run_stat_pace),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                val steps = runData.steps
                if (steps != null) {
                    StatTile(
                        value = formatSteps(steps),
                        label = stringResource(R.string.active_run_stat_steps),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
                val heartRate = runData.heartRate
                if (heartRate > 0) {
                    StatTile(
                        value = heartRate.toString(),
                        label = stringResource(R.string.active_run_stat_heart_rate),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun SignalLostBanner(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = MaterialTheme.spacing.small,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        Icon(
            imageVector = ErrorIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(MaterialTheme.spacing.large),
        )
        Text(
            text = stringResource(R.string.active_run_signal_lost),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun RecordingIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = MaterialTheme.motion.reduceMotion
    val alpha =
        if (isActive && !reduceMotion) {
            val transition = rememberInfiniteTransition(label = "recording")
            val pulse by transition.animateFloat(
                initialValue = 1f,
                targetValue = 0.3f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 800),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "recording-alpha",
            )
            pulse
        } else if (isActive) {
            1f
        } else {
            0.3f
        }

    Box(
        modifier =
            modifier
                .size(MaterialTheme.spacing.small)
                .graphicsLayer { this.alpha = alpha }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
    )
}

@Composable
private fun RunControls(
    runPhase: RunPhase,
    onToggleRun: () -> Unit,
    onFinishRun: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasStarted = runPhase == RunPhase.Tracking || runPhase == RunPhase.Paused
    val motion = MaterialTheme.motion

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        GyuRunActionButton(
            text = stringResource(toggleButtonLabelRes(runPhase)),
            isLoading = runPhase == RunPhase.Saving,
            enabled = runPhase != RunPhase.Saving,
            leadingIcon = if (runPhase == RunPhase.Tracking) PauseIcon else PlayIcon,
            onClick = onToggleRun,
            modifier = Modifier.weight(1f),
        )

        AnimatedVisibility(
            visible = hasStarted,
            enter = motion.contentEnter,
            exit = motion.contentExit,
        ) {
            GyuRunOutlinedActionButton(
                text = stringResource(R.string.active_run_finish),
                isLoading = false,
                onClick = onFinishRun,
            )
        }
    }
}

@Composable
private fun SavingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        GyuRunCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MaterialTheme.spacing.large),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.active_run_saving),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

internal fun formatElapsedTime(elapsedTime: Duration): String =
    elapsedTime.toComponents { hours, minutes, seconds, _ ->
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

internal fun isSignalLost(
    isLocationAvailable: Boolean,
    runPhase: RunPhase,
): Boolean =
    !isLocationAvailable &&
            (runPhase == RunPhase.Tracking || runPhase == RunPhase.Paused)

internal fun toggleButtonLabelRes(runPhase: RunPhase): Int =
    when (runPhase) {
        RunPhase.Tracking -> R.string.active_run_pause
        RunPhase.Paused -> R.string.active_run_resume
        else -> R.string.active_run_start
    }

@PreviewLightDark
@Composable
private fun RunStatsCardPreview() {
    GyuRunTheme {
        RunStatsCard(
            runData =
                RunData(
                    distanceMeters = 2_340,
                    pace = 330.seconds,
                    steps = 3_120,
                    heartRate = 142,
                ),
            elapsedTime = 754.seconds,
            isTracking = true,
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        )
    }
}

@PreviewLightDark
@Composable
private fun SignalLostBannerPreview() {
    GyuRunTheme {
        SignalLostBanner(modifier = Modifier.padding(MaterialTheme.spacing.medium))
    }
}

@PreviewLightDark
@Composable
private fun ResumeRunDialogPreview() {
    GyuRunTheme {
        ResumeRunDialog(
            onResume = {},
            onDiscard = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun RunStatsCardWithoutStepsAndHeartRatePreview() {
    GyuRunTheme {
        RunStatsCard(
            runData =
                RunData(
                    distanceMeters = 2_340,
                    pace = 330.seconds,
                ),
            elapsedTime = 754.seconds,
            isTracking = false,
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        )
    }
}

@PreviewLightDark
@Composable
private fun RunControlsNotStartedPreview() {
    RunControlsPreviewFrame(runPhase = RunPhase.NotStarted)
}

@PreviewLightDark
@Composable
private fun RunControlsTrackingPreview() {
    RunControlsPreviewFrame(runPhase = RunPhase.Tracking)
}

@PreviewLightDark
@Composable
private fun RunControlsPausedPreview() {
    RunControlsPreviewFrame(runPhase = RunPhase.Paused)
}

@PreviewLightDark
@Composable
private fun RunControlsSavingPreview() {
    RunControlsPreviewFrame(runPhase = RunPhase.Saving)
}

@Composable
private fun RunControlsPreviewFrame(runPhase: RunPhase) {
    GyuRunTheme {
        RunControls(
            runPhase = runPhase,
            onToggleRun = {},
            onFinishRun = {},
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        )
    }
}

@PreviewLightDark
@Composable
private fun RecordingIndicatorPreview() {
    GyuRunTheme {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            RecordingIndicator(isActive = true)
            RecordingIndicator(isActive = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun SavingOverlayPreview() {
    GyuRunTheme {
        SavingOverlay(modifier = Modifier.size(MaterialTheme.spacing.huge * 5))
    }
}