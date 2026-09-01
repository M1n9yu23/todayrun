package com.gyugle.gyurun.feature.details.presentation.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.Split
import com.gyugle.gyurun.core.domain.run.calculator.RunSplitCalculator
import com.gyugle.gyurun.core.domain.weather.Weather
import com.gyugle.gyurun.core.domain.weather.WeatherType
import com.gyugle.gyurun.core.map.MapView
import com.gyugle.gyurun.core.map.RunMapState
import com.gyugle.gyurun.core.presentation.designsystem.ErrorIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.WeatherClearIcon
import com.gyugle.gyurun.core.presentation.designsystem.WeatherCloudyIcon
import com.gyugle.gyurun.core.presentation.designsystem.WeatherFogIcon
import com.gyugle.gyurun.core.presentation.designsystem.WeatherRainIcon
import com.gyugle.gyurun.core.presentation.designsystem.WeatherSnowIcon
import com.gyugle.gyurun.core.presentation.designsystem.WeatherThunderstormIcon
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunAsyncImage
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunCard
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunEmptyState
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunScaffold
import com.gyugle.gyurun.core.presentation.designsystem.components.GyuRunToolbar
import com.gyugle.gyurun.core.presentation.designsystem.components.StatTile
import com.gyugle.gyurun.core.presentation.designsystem.motion
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.designsystem.statLarge
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatDuration
import com.gyugle.gyurun.core.presentation.ui.formatElevation
import com.gyugle.gyurun.core.presentation.ui.formatHumidity
import com.gyugle.gyurun.core.presentation.ui.formatPace
import com.gyugle.gyurun.core.presentation.ui.formatRunDate
import com.gyugle.gyurun.core.presentation.ui.formatSpeed
import com.gyugle.gyurun.core.presentation.ui.formatSteps
import com.gyugle.gyurun.core.presentation.ui.formatTemperature
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.time.ZonedDateTime
import java.time.format.FormatStyle
import kotlin.collections.forEachIndexed
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal const val RUN_DETAIL_CONTENT_TAG = "run_detail_content"

private const val ROUTE_MAP_ASPECT_RATIO = 16f / 9f

@Composable
internal fun RunDetailScreenRoot(
    runId: String,
    onBackClick: () -> Unit,
    viewModel: RunDetailViewModel = koinViewModel { parametersOf(runId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RunDetailScreen(
        state = state,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun RunDetailScreen(
    state: RunDetailState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GyuRunScaffold(
        modifier = modifier,
        topBar = {
            GyuRunToolbar(
                title = stringResource(R.string.detail_title),
                showBackButton = true,
                onBackClick = onBackClick,
            )
        },
    ) { padding ->
        val contentModifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
        val motion = MaterialTheme.motion
        when (state) {
            RunDetailState.Loading -> {
                Box(
                    modifier = contentModifier,
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            RunDetailState.NotFound -> {
                GyuRunEmptyState(
                    icon = ErrorIcon,
                    title = stringResource(R.string.detail_not_found_title),
                    message = stringResource(R.string.detail_not_found_message),
                    modifier = contentModifier,
                )
            }

            is RunDetailState.Content -> {
                val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = motion.contentEnter,
                    exit = motion.contentExit,
                ) {
                    RunDetailContent(
                        run = state.run,
                        modifier = contentModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun RunDetailContent(
    run: Run,
    modifier: Modifier = Modifier,
) {
    val splits = remember(run.route) { RunSplitCalculator.calculateSplits(run.route) }
    Column(
        modifier =
            modifier
                .testTag(RUN_DETAIL_CONTENT_TAG)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
    ) {
        Text(
            text = formatRunDate(run.dateTimeUtc, FormatStyle.FULL),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        val routeModifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(ROUTE_MAP_ASPECT_RATIO)
        if (run.route.isNotEmpty()) {
            val mapState =
                remember(run.route) {
                    RunMapState(
                        locations =
                            run.route.map { segment ->
                                segment.map { it.location.location }
                            },
                    )
                }
            MapView(
                state = mapState,
                modifier = routeModifier,
            )
        } else {
            GyuRunAsyncImage(
                model = run.mapPicturePath?.let { File(it) },
                contentDescription = stringResource(R.string.detail_route_map),
                modifier = routeModifier,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
        }
        RunSummaryCard(run = run)
        run.weather?.let { weather ->
            WeatherCard(weather = weather)
        }
        if (splits.isNotEmpty()) {
            SplitsCard(splits = splits)
        }
    }
}

@Composable
internal fun RunSummaryCard(
    run: Run,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    GyuRunCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                StatTile(
                    label = stringResource(R.string.detail_stat_distance),
                    value = formatDistance(run.distanceMeters, unit),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(R.string.detail_stat_duration),
                    value = formatDuration(run.duration),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(R.string.detail_stat_avg_pace),
                    value = formatPace(run.distanceMeters, run.duration, unit),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                StatTile(
                    label = stringResource(R.string.detail_stat_max_speed),
                    value = formatSpeed(run.maxSpeedKmh, unit),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(R.string.detail_stat_elevation),
                    value = formatElevation(run.totalElevationMeters),
                    valueStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                val steps = run.steps
                if (steps != null) {
                    StatTile(
                        label = stringResource(R.string.detail_stat_steps),
                        value = formatSteps(steps),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    GyuRunCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        ) {
            Text(
                text = stringResource(R.string.detail_weather_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = weatherTypeIcon(weather.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(38.dp),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                ) {
                    Text(
                        text = formatTemperature(weather.temperatureCelsius),
                        style = MaterialTheme.typography.statLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(weatherTypeLabel(weather.type)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(vertical = MaterialTheme.spacing.medium),
                ) {
                    StatTile(
                        label = stringResource(R.string.detail_weather_feels_like),
                        value = formatTemperature(weather.feelsLikeCelsius),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    WeatherMetricDivider()
                    StatTile(
                        label = stringResource(R.string.detail_weather_humidity),
                        value = formatHumidity(weather.humidityPercent),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    WeatherMetricDivider()
                    StatTile(
                        label = stringResource(R.string.detail_weather_wind),
                        value = formatSpeed(weather.windSpeedKmh, unit),
                        valueStyle = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricDivider() {
    VerticalDivider(
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(horizontal = MaterialTheme.spacing.small),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    )
}

@Composable
internal fun SplitsCard(
    splits: List<Split>,
    modifier: Modifier = Modifier,
) {
    val maxPaceSeconds = splits.maxOf { paceSecondsPerKm(it) }
    GyuRunCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            Text(
                text = stringResource(R.string.detail_splits_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            splits.forEachIndexed { index, split ->
                SplitRow(
                    number = index + 1,
                    split = split,
                    maxPaceSeconds = maxPaceSeconds,
                )
            }
        }
    }
}

@Composable
private fun SplitRow(
    number: Int,
    split: Split,
    maxPaceSeconds: Double,
    modifier: Modifier = Modifier,
) {
    val unit = LocalDistanceUnit.current
    val fraction = splitBarFraction(split, maxPaceSeconds)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.detail_split_number, number),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.widthIn(min = MaterialTheme.spacing.extraLarge),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
            Text(
                text = formatPace(split.distanceMeters, split.duration, unit),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.small)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
        Text(
            text = formatElevation(split.elevationGainMeters),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal fun paceSecondsPerKm(split: Split): Double {
    if (split.distanceMeters <= 0) return 0.0
    return split.duration.inWholeSeconds / (split.distanceMeters / 1000.0)
}

internal fun splitBarFraction(
    split: Split,
    maxPaceSeconds: Double,
): Float {
    if (maxPaceSeconds <= 0.0) return 0f
    return (paceSecondsPerKm(split) / maxPaceSeconds).toFloat()
}

@Composable
internal fun weatherTypeIcon(type: WeatherType): ImageVector =
    when (type) {
        WeatherType.CLEAR -> WeatherClearIcon
        WeatherType.CLOUDY, WeatherType.UNKNOWN -> WeatherCloudyIcon
        WeatherType.FOG -> WeatherFogIcon
        WeatherType.RAIN -> WeatherRainIcon
        WeatherType.SNOW -> WeatherSnowIcon
        WeatherType.THUNDERSTORM -> WeatherThunderstormIcon
    }

private fun weatherTypeLabel(type: WeatherType): Int =
    when (type) {
        WeatherType.CLEAR -> R.string.detail_weather_clear
        WeatherType.CLOUDY -> R.string.detail_weather_cloudy
        WeatherType.FOG -> R.string.detail_weather_fog
        WeatherType.RAIN -> R.string.detail_weather_rain
        WeatherType.SNOW -> R.string.detail_weather_snow
        WeatherType.THUNDERSTORM -> R.string.detail_weather_thunderstorm
        WeatherType.UNKNOWN -> R.string.detail_weather_unknown
    }

@PreviewLightDark
@Composable
private fun RunDetailContentPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RunDetailContent(run = previewRun())
        }
    }
}

@PreviewLightDark
@Composable
private fun RunSummaryCardWithStepsPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RunSummaryCard(
                run = previewRun(),
                modifier = Modifier.padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RunSummaryCardWithoutStepsPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RunSummaryCard(
                run = previewRun().copy(steps = null),
                modifier = Modifier.padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WeatherCardClearPreview() {
    WeatherCardPreviewFrame(type = WeatherType.CLEAR)
}

@PreviewLightDark
@Composable
private fun WeatherCardRainPreview() {
    WeatherCardPreviewFrame(type = WeatherType.RAIN)
}

@PreviewLightDark
@Composable
private fun WeatherCardSnowPreview() {
    WeatherCardPreviewFrame(type = WeatherType.SNOW)
}

@PreviewLightDark
@Composable
private fun WeatherCardUnknownPreview() {
    WeatherCardPreviewFrame(type = WeatherType.UNKNOWN)
}

@Composable
private fun WeatherCardPreviewFrame(type: WeatherType) {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            WeatherCard(
                weather = previewWeather.copy(type = type),
                modifier = Modifier.padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SplitsCardPreview() {
    GyuRunTheme {
        SplitsCard(
            splits =
                listOf(
                    Split(distanceMeters = 1000, duration = (5 * 60 + 30).seconds, elevationGainMeters = 12),
                    Split(distanceMeters = 1000, duration = (5 * 60 + 12).seconds, elevationGainMeters = 4),
                    Split(distanceMeters = 1000, duration = (6 * 60 + 2).seconds, elevationGainMeters = 20),
                    Split(distanceMeters = 430, duration = (2 * 60 + 40).seconds, elevationGainMeters = 3),
                ),
        )
    }
}

@PreviewLightDark
@Composable
private fun RunDetailNotFoundPreview() {
    GyuRunTheme {
        RunDetailScreen(
            state = RunDetailState.NotFound,
            onBackClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun RunDetailLoadingPreview() {
    GyuRunTheme {
        RunDetailScreen(
            state = RunDetailState.Loading,
            onBackClick = {},
        )
    }
}

private val previewWeather: Weather =
    Weather(
        temperatureCelsius = 21.0,
        feelsLikeCelsius = 20.0,
        humidityPercent = 60,
        windSpeedKmh = 8.5,
        type = WeatherType.CLEAR,
    )

private fun previewRun(): Run =
    Run(
        id = RunId("preview-1"),
        duration = 32.minutes,
        dateTimeUtc = ZonedDateTime.parse("2026-07-04T08:30:00Z"),
        distanceMeters = 5230,
        location = Location(lat = 0.0, long = 0.0),
        maxSpeedKmh = 12.4,
        totalElevationMeters = 48,
        steps = 4_820,
        weather = previewWeather,
    )
