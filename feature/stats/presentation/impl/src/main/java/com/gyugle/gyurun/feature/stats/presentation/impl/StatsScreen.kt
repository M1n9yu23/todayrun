package com.gyugle.gyurun.feature.stats.presentation.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.gyugle.gyurun.core.domain.run.RunStatistics
import com.gyugle.gyurun.core.domain.run.WeeklyDistance
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.StatsIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunCard
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunEmptyState
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunToolbar
import com.gyugle.gyurun.core.presentation.designsystem.components.StatTile
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatDuration
import com.gyugle.gyurun.core.presentation.ui.formatElevation
import com.gyugle.gyurun.core.presentation.ui.formatSpeed
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun StatsScreenRoot(
    onBackClick: () -> Unit,
    viewModel: StatsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatsScreen(
        state = state,
        onBackClick = onBackClick,
    )
}

@Composable
private fun StatsScreen(
    state: StatsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWide =
        currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    GyuRunScaffold(
        modifier = modifier,
        topBar = {
            GyuRunToolbar(
                title = stringResource(R.string.stats_title),
                showBackButton = true,
                onBackClick = onBackClick,
            )
        },
    ) { padding ->
        when (state) {
            StatsState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            StatsState.Empty -> {
                GyuRunEmptyState(
                    icon = StatsIcon,
                    title = stringResource(R.string.stats_empty_title),
                    message = stringResource(R.string.stats_empty_message),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }

            is StatsState.Content -> {
                StatsContent(
                    statistics = state.statistics,
                    weeklyDistances = state.weeklyDistances,
                    isWide = isWide,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }
        }
    }
}

@Composable
internal fun StatsContent(
    statistics: RunStatistics,
    weeklyDistances: List<WeeklyDistance>,
    isWide: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
    ) {
        if (isWide) {
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                ) {
                    TotalDistanceHero(statistics = statistics)
                    StatTiles(statistics = statistics)
                }
                WeeklyChartCard(
                    weeklyDistances = weeklyDistances,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            TotalDistanceHero(statistics = statistics)
            StatTiles(statistics = statistics)
            WeeklyChartCard(
                weeklyDistances = weeklyDistances,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal fun TotalDistanceHero(
    statistics: RunStatistics,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    Column(modifier = modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = stringResource(R.string.stats_total_distance),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = formatDistance(statistics.totalDistanceMeters, unit),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun StatTiles(
    statistics: RunStatistics,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
            GyuRunCard(modifier = Modifier.weight(1f)) {
                StatTile(
                    value = statistics.totalRuns.toString(),
                    label = stringResource(R.string.stats_total_runs),
                )
            }
            GyuRunCard(modifier = Modifier.weight(1f)) {
                StatTile(
                    value = formatDuration(statistics.totalDuration),
                    label = stringResource(R.string.stats_total_duration),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
            GyuRunCard(modifier = Modifier.weight(1f)) {
                StatTile(
                    value = formatSpeed(statistics.averageSpeedKmh, unit),
                    label = stringResource(R.string.stats_avg_speed),
                )
            }
            GyuRunCard(modifier = Modifier.weight(1f)) {
                StatTile(
                    value = formatElevation(statistics.totalElevationMeters),
                    label = stringResource(R.string.stats_total_elevation),
                )
            }
        }
    }
}

@Composable
internal fun WeeklyChartCard(
    weeklyDistances: List<WeeklyDistance>,
    modifier: Modifier = Modifier,
) {
    GyuRunCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = stringResource(R.string.stats_weekly_distance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() },
            )
            WeeklyDistanceChart(weeks = weeklyDistances)
        }
    }
}

@PreviewLightDark
@Composable
private fun StatsScreenLoadingPreview() {
    GyuRunTheme {
        StatsScreen(
            state = StatsState.Loading,
            onBackClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun StatsScreenEmptyPreview() {
    GyuRunTheme {
        StatsScreen(
            state = StatsState.Empty,
            onBackClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun StatsContentPreview() {
    GyuRunTheme {
        StatsScreen(
            state =
                StatsState.Content(
                    statistics = previewStatistics,
                    weeklyDistances = previewWeeklyDistances(),
                ),
            onBackClick = {},
        )
    }
}

@Preview(widthDp = 1000, heightDp = 700)
@Composable
private fun StatsContentWidePreview() {
    GyuRunTheme {
        Surface {
            StatsContent(
                statistics = previewStatistics,
                weeklyDistances = previewWeeklyDistances(),
                isWide = true,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TotalDistanceHeroPreview() {
    GyuRunTheme {
        Surface {
            TotalDistanceHero(
                statistics = previewStatistics,
                modifier = Modifier.padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StatTilesPreview() {
    GyuRunTheme {
        Surface {
            StatTiles(
                statistics = previewStatistics,
                modifier = Modifier.padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WeeklyChartCardPreview() {
    GyuRunTheme {
        Surface {
            WeeklyChartCard(
                weeklyDistances = previewWeeklyDistances(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.large),
            )
        }
    }
}
