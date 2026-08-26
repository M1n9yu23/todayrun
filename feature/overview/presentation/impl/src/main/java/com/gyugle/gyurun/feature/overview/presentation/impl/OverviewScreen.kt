package com.gyugle.gyurun.feature.overview.presentation.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.window.core.layout.WindowSizeClass
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.presentation.designsystem.ArrowUpIcon
import com.gyugle.gyurun.core.presentation.designsystem.DeleteIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.PlayIcon
import com.gyugle.gyurun.core.presentation.designsystem.RunIcon
import com.gyugle.gyurun.core.presentation.designsystem.SettingsIcon
import com.gyugle.gyurun.core.presentation.designsystem.StatsIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunAsyncImage
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunCard
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunEmptyState
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunErrorState
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunListLoading
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunTextButton
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunToolbar
import com.gyugle.gyurun.core.presentation.designsystem.components.StatTile
import com.gyugle.gyurun.core.presentation.designsystem.motion
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.ObserveAsEvents
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatDuration
import com.gyugle.gyurun.core.presentation.ui.formatPace
import com.gyugle.gyurun.core.presentation.ui.formatRunDate
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun OverviewScreenRoot(
    onStartRun: () -> Unit,
    onOpenRun: (String) -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: OverviewViewModel = koinViewModel(),
) {
    val runs = viewModel.runs.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val deletedMessage = stringResource(R.string.overview_run_deleted)
    val undoLabel = stringResource(R.string.overview_undo)

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is OverviewEvent.RunDeleted -> {
                scope.launch {
                    val result =
                        snackbarHostState.showSnackbar(
                            message = deletedMessage,
                            actionLabel = undoLabel,
                            duration = SnackbarDuration.Long,
                        )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoDelete(event.run)
                    } else {
                        viewModel.onConfirmDelete(event.run)
                    }
                }
            }

            is OverviewEvent.UndoFailed -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.error.asString(context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    OverviewScreen(
        runs = runs,
        snackbarHostState = snackbarHostState,
        onStartRun = onStartRun,
        onOpenStats = onOpenStats,
        onOpenSettings = onOpenSettings,
        onDeleteRun = viewModel::onDeleteRun,
        onRunClick = { run -> run.id?.let { onOpenRun(it.value) } },
    )
}

@Composable
private fun OverviewScreen(
    runs: LazyPagingItems<Run>,
    snackbarHostState: SnackbarHostState,
    onStartRun: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onDeleteRun: (Run) -> Unit,
    onRunClick: (Run) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 0 }
    }

    val motion = MaterialTheme.motion
    val itemFadeInSpec =
        remember(motion) { tween<Float>(motion.durationMedium, easing = motion.standardEasing) }
    val itemFadeOutSpec =
        remember(motion) { tween<Float>(motion.durationShort, easing = motion.standardEasing) }
    val itemPlacementSpec =
        remember(motion) {
            if (motion.reduceMotion) {
                null
            } else {
                tween<IntOffset>(motion.durationMedium, easing = motion.emphasizedEasing)
            }
        }

    GyuRunScaffold(
        modifier = modifier,
        topBar = {
            GyuRunToolbar(
                title = stringResource(R.string.overview_title),
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(
                            imageVector = StatsIcon,
                            contentDescription = stringResource(R.string.overview_stats),
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = SettingsIcon,
                            contentDescription = stringResource(R.string.overview_settings),
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = motion.contentEnter,
                    exit = motion.contentExit,
                ) {
                    SmallFloatingActionButton(
                        onClick = { scope.launch { gridState.animateScrollToItem(0) } },
                    ) {
                        Icon(
                            imageVector = ArrowUpIcon,
                            contentDescription = stringResource(R.string.overview_scroll_to_top),
                        )
                    }
                }
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.overview_start_run)) },
                    icon = { Icon(imageVector = PlayIcon, contentDescription = null) },
                    onClick = onStartRun,
                )
            }
        },
    ) { padding ->

        val refresh = runs.loadState.refresh
        when {
            refresh is LoadState.Loading -> {
                GyuRunListLoading(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }

            refresh is LoadState.Error -> {
                GyuRunErrorState(
                    message = stringResource(R.string.overview_error),
                    onRetry = { runs.retry() },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }

            runs.itemCount == 0 -> {
                GyuRunEmptyState(
                    icon = RunIcon,
                    title = stringResource(R.string.overview_empty_title),
                    message = stringResource(R.string.overview_empty_message),
                    actionLabel = stringResource(R.string.overview_start_run),
                    onActionClick = onStartRun,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }

            else -> {
                val columns =
                    if (currentWindowAdaptiveInfo()
                            .windowSizeClass
                            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
                    ) {
                        2
                    } else {
                        1
                    }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columns),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentPadding = PaddingValues(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    items(
                        count = runs.itemCount,
                        key = runs.itemKey { it.id?.value.orEmpty() },
                    ) { index ->
                        val run = runs[index]
                        if (run != null) {
                            SwipeableRunRow(
                                run = run,
                                onDelete = onDeleteRun,
                                onClick = { onRunClick(run) },
                                modifier =
                                    Modifier.animateItem(
                                        fadeInSpec = itemFadeInSpec,
                                        placementSpec = itemPlacementSpec,
                                        fadeOutSpec = itemFadeOutSpec,
                                    ),
                            )
                        }
                    }

                    when (runs.loadState.append) {
                        is LoadState.Loading -> {
                            item(
                                key = "append_loading",
                                span = { GridItemSpan(maxLineSpan) },
                            ) { LoadingFooter() }
                        }

                        is LoadState.Error -> {
                            item(
                                key = "append_error",
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                AppendErrorFooter(onRetry = { runs.retry() })
                            }
                        }

                        else -> {
                            Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeableRunRow(
    run: Run,
    onDelete: (Run) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(run)
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            DeleteBackground(active = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
        },
    ) {
        RunListRow(run = run, onClick = onClick)
    }
}

@Composable
private fun DeleteBackground(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val color =
        if (active) MaterialTheme.colorScheme.errorContainer else Color.Transparent
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.large)
                .background(color)
                .padding(horizontal = MaterialTheme.spacing.large),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (active) {
            Icon(
                imageVector = DeleteIcon,
                contentDescription = stringResource(R.string.overview_delete_run),
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun LoadingFooter(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppendErrorFooter(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.overview_load_more_error),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        GyuRunTextButton(
            text = stringResource(R.string.overview_retry),
            onClick = onRetry,
        )
    }
}

@Composable
internal fun RunListRow(
    run: Run,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    GyuRunCard(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = formatRunDate(run.dateTimeUtc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            GyuRunAsyncImage(
                model = run.mapPicturePath?.let { File(it) },
                contentDescription = stringResource(R.string.overview_route_map),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(ROUTE_THUMBNAIL_ASPECT_RATIO),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                StatTile(
                    value = formatDistance(run.distanceMeters, unit),
                    label = stringResource(R.string.overview_stat_distance),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = formatDuration(run.duration),
                    label = stringResource(R.string.overview_stat_duration),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = formatPace(run.distanceMeters, run.duration, unit),
                    label = stringResource(R.string.overview_stat_pace),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private const val ROUTE_THUMBNAIL_ASPECT_RATIO = 16f / 9f

@PreviewLightDark
@Composable
private fun OverviewScreenPreview() {
    val runs =
        flowOf(
            PagingData.from(
                listOf(
                    previewRun(id = "preview-1", distanceMeters = 5230, minutes = 32),
                    previewRun(id = "preview-2", distanceMeters = 8120, minutes = 51),
                ),
            ),
        ).collectAsLazyPagingItems()
    GyuRunTheme {
        OverviewScreen(
            runs = runs,
            snackbarHostState = remember { SnackbarHostState() },
            onStartRun = {},
            onOpenStats = {},
            onOpenSettings = {},
            onDeleteRun = {},
            onRunClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun RunListRowPreview() {
    GyuRunTheme {
        RunListRow(
            run = previewRun(id = "preview-1", distanceMeters = 5230, minutes = 32),
            onClick = {},
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        )
    }
}

@PreviewLightDark
@Composable
private fun SwipeableRunRowPreview() {
    GyuRunTheme {
        SwipeableRunRow(
            run = previewRun(id = "preview-1", distanceMeters = 5230, minutes = 32),
            onDelete = {},
            onClick = {},
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
        )
    }
}

@PreviewLightDark
@Composable
private fun DeleteBackgroundActivePreview() {
    GyuRunTheme {
        DeleteBackground(
            active = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.huge * 2),
        )
    }
}

@PreviewLightDark
@Composable
private fun DeleteBackgroundIdlePreview() {
    GyuRunTheme {
        DeleteBackground(
            active = false,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.huge * 2),
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingFooterPreview() {
    GyuRunTheme {
        LoadingFooter()
    }
}

@PreviewLightDark
@Composable
private fun AppendErrorFooterPreview() {
    GyuRunTheme {
        AppendErrorFooter(onRetry = {})
    }
}

private fun previewRun(
    id: String,
    distanceMeters: Int,
    minutes: Int,
): Run =
    Run(
        id = RunId(id),
        duration = minutes.minutes,
        dateTimeUtc = ZonedDateTime.parse("2026-07-04T08:30:00Z"),
        distanceMeters = distanceMeters,
        location = Location(lat = 0.0, long = 0.0),
        maxSpeedKmh = 12.4,
        totalElevationMeters = 48,
    )