package com.gyugle.gyurun.wear.run.presentation

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.FilledIconButton
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OutlinedIconButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import com.gyugle.gyurun.core.presentation.designsystem.ErrorIcon
import com.gyugle.gyurun.core.presentation.designsystem.LocalSpacing
import com.gyugle.gyurun.core.presentation.designsystem.PauseIcon
import com.gyugle.gyurun.core.presentation.designsystem.PlayIcon
import com.gyugle.gyurun.core.presentation.ui.ObserveAsEvents
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatDuration
import com.gyugle.gyurun.wear.designsystem.FinishIcon
import com.gyugle.gyurun.wear.designsystem.theme.WearGyuRunTheme
import com.gyugle.gyurun.wear.run.presentation.components.RunDataCard
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun TrackerScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: TrackerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity

    var askedPermission by rememberSaveable { mutableStateOf<String?>(null) }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { _ ->
            viewModel.onAction(activity.permissionResult(justAsked = askedPermission))
        }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onAction(activity.permissionResult(justAsked = null))
        }
    }

    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is TrackerEvent.Error -> {
                Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
            }

            TrackerEvent.RunFinished -> {
                Unit
            }
        }
    }

    TrackerScreen(
        state = viewModel.state,
        onAction = { action ->
            if (action == TrackerAction.OnExercisePermissionClick) {
                permissionToAsk(viewModel.state)?.let { permission ->
                    askedPermission = permission
                    permissionLauncher.launch(permission)
                }
            }
            viewModel.onAction(action)
        },
        modifier = modifier,
    )
}

@Composable
internal fun TrackerScreen(
    state: TrackerState,
    onAction: (TrackerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(modifier = modifier) {
        if (!state.isConnectedPhoneNearby) {
            NotConnectedScreen()
        } else {
            val pagerState = rememberPagerState(pageCount = { 2 })
            HorizontalPagerScaffold(pagerState = pagerState) {
                HorizontalPager(state = pagerState) { page ->
                    AnimatedPage(pageIndex = page, pagerState = pagerState) {
                        when (page) {
                            0 -> MetricsPage(state = state)
                            else -> ControlsPage(state = state, onAction = onAction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MetricsPage(
    state: TrackerState,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val heartRateLabelRes = heartRateValueRes(state)
    ScreenScaffold(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = formatDuration(state.elapsedDuration),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                RunDataCard(
                    title = stringResource(R.string.wear_metric_heart_rate),
                    value =
                        heartRateLabelRes
                            ?.let { stringResource(it) }
                            ?: formatHeartRate(state.heartRate),
                    valueColor =
                        when (heartRateLabelRes) {
                            null -> MaterialTheme.colorScheme.onSurface
                            R.string.wear_heart_rate_acquiring -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.error
                        },
                    modifier = Modifier.weight(1f),
                )
                RunDataCard(
                    title = stringResource(R.string.wear_metric_distance),
                    value = formatDistance(state.distanceMeters, DistanceUnit.KILOMETERS),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun ControlsPage(
    state: TrackerState,
    onAction: (TrackerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    ScreenScaffold(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!state.isTrackable && !state.hasStartedRunning) {
                Text(
                    text = stringResource(R.string.wear_open_phone_run),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    if (state.isTrackable) {
                        ToggleRunButton(
                            isRunActive = state.isRunActive,
                            onClick = { onAction(TrackerAction.OnToggleRunClick) },
                        )
                    }
                    if (canFinishRun(state)) {
                        FilledIconButton(
                            onClick = { onAction(TrackerAction.OnFinishRunClick) },
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                ),
                        ) {
                            Icon(
                                imageVector = FinishIcon,
                                contentDescription = stringResource(R.string.wear_control_finish),
                            )
                        }
                    }
                }
            }
            recordingBlockedRes(state)?.let { messageRes ->
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = stringResource(messageRes),
                    style = MaterialTheme.typography.bodyExtraSmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
            exercisePermissionButtonRes(state)?.let { labelRes ->
                Spacer(modifier = Modifier.height(spacing.small))
                CompactButton(
                    onClick = { onAction(TrackerAction.OnExercisePermissionClick) },
                    enabled = !state.isRequestingPermission,
                    label = { Text(text = stringResource(labelRes)) },
                )
            }
        }
    }
}

@Composable
private fun ToggleRunButton(
    isRunActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (isRunActive) PauseIcon else PlayIcon,
            contentDescription =
                stringResource(
                    if (isRunActive) R.string.wear_control_pause else R.string.wear_control_start,
                ),
        )
    }
}

@Composable
internal fun NotConnectedScreen(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    ScreenScaffold(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = ErrorIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = stringResource(R.string.wear_connect_phone),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun ComponentActivity.permissionResult(justAsked: String?): TrackerAction.OnPermissionResult =
    TrackerAction.OnPermissionResult(
        activityPermission = permissionStateOf(ACTIVITY_PERMISSION, justAsked),
        heartRatePermission = permissionStateOf(heartRatePermission(), justAsked),
    )

private fun ComponentActivity.permissionStateOf(
    permission: String,
    justAsked: String?,
): PermissionState {
    val isGranted =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    val shouldShowRationale = shouldShowRequestPermissionRationale(permission)
    return if (permission == justAsked) {
        permissionStateAfterRequest(isGranted, shouldShowRationale)
    } else {
        permissionStateWhenAsked(isGranted, shouldShowRationale)
    }
}

private fun formatHeartRate(heartRate: Int): String = if (heartRate <= 0) "—" else "$heartRate"

internal val runningState =
    TrackerState(
        isConnectedPhoneNearby = true,
        isTrackable = true,
        canTrackHeartRate = true,
        activityPermission = PermissionState.GRANTED,
        heartRatePermission = PermissionState.GRANTED,
        isRunActive = true,
        hasStartedRunning = true,
        heartRate = 142,
        distanceMeters = 2340,
        elapsedDuration = 754.seconds,
    )

@WearPreviewDevices
@Composable
private fun TrackerScreenMetricsPreview() {
    WearGyuRunTheme {
        TrackerScreen(state = runningState, onAction = {})
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenHeartRateAcquiringPreview() {
    WearGyuRunTheme {
        TrackerScreen(
            state = runningState.copy(isHeartRateAvailable = false, heartRate = 0),
            onAction = {},
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenNotConnectedPreview() {
    WearGyuRunTheme {
        TrackerScreen(state = TrackerState(isConnectedPhoneNearby = false), onAction = {})
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenNotTrackablePreview() {
    WearGyuRunTheme {
        TrackerScreen(
            state = runningState.copy(
                isTrackable = false,
                isRunActive = false,
                hasStartedRunning = false
            ),
            onAction = {},
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenNoHeartRatePermissionPreview() {
    WearGyuRunTheme {
        TrackerScreen(
            state =
                runningState.copy(
                    canTrackHeartRate = false,
                    heartRatePermission = PermissionState.DENIED,
                ),
            onAction = {},
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenNoRecordingPermissionPreview() {
    WearGyuRunTheme {
        TrackerScreen(
            state =
                runningState.copy(
                    canTrackHeartRate = false,
                    activityPermission = PermissionState.DENIED,
                    heartRatePermission = PermissionState.DENIED,
                ),
            onAction = {},
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenBeforeFirstStartPreview() {
    WearGyuRunTheme {
        TrackerScreen(
            state = runningState.copy(isRunActive = false, hasStartedRunning = false),
            onAction = {},
        )
    }
}

@WearPreviewDevices
@Composable
private fun TrackerScreenUntrackableMidRunPreview() {
    WearGyuRunTheme {
        TrackerScreen(state = runningState.copy(isTrackable = false), onAction = {})
    }
}