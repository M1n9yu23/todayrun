package com.gyugle.gyurun.widget

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.gyugle.gyurun.MainActivity
import com.gyugle.gyurun.R
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.preferences.UserSettings
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.WeeklyRunSummary
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunDarkColorScheme
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunLightColorScheme
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.formatRunDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

private val GyuRunGlanceColors =
    ColorProviders(
        light = GyuRunLightColorScheme,
        dark = GyuRunDarkColorScheme,
    )

class WeeklyStatsWidget :
    GlanceAppWidget(),
    KoinComponent {
    private val runRepository: RunRepository by inject()
    private val userSettingsRepository: UserSettingsRepository by inject()
    private val widgetLanguage: WidgetLanguage by inject()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val weekAgo = WeeklyRunSummary.weekAgo(ZonedDateTime.now())
        provideContent {
            val weekRuns by runRepository.getRunsSince(weekAgo).collectAsState(initial = emptyList())
            val mostRecentRun by runRepository.getMostRecentRun().collectAsState(initial = null)
            val settings by userSettingsRepository.userSettings.collectAsState(initial = UserSettings())
            val languageTags by widgetLanguage.languageTags.collectAsState(initial = "")

            CompositionLocalProvider(LocalContext provides context.inLanguage(languageTags)) {
                GlanceTheme(colors = GyuRunGlanceColors) {
                    WeeklyStatsContent(
                        summary = WeeklyRunSummary.ofWeekRuns(weekRuns, mostRecentRun),
                        unit = settings.distanceUnit,
                    )
                }
            }
        }
    }
}

class WeeklyStatsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeeklyStatsWidget()
}

internal fun Context.inLanguage(languageTags: String): Context {
    if (languageTags.isBlank()) return this
    val configuration = Configuration(resources.configuration)
    configuration.setLocales(LocaleList.forLanguageTags(languageTags))
    return createConfigurationContext(configuration)
}

@Composable
private fun WeeklyStatsContent(
    summary: WeeklyRunSummary,
    unit: DistanceUnit,
) {
    val context = LocalContext.current
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_background))
                .padding(16.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
    ) {
        WidgetHeader()
        Spacer(GlanceModifier.height(8.dp))

        if (summary.mostRecentRun == null) {
            EmptyState()
        } else {
            WeeklySummary(summary = summary, unit = unit)
        }
    }
}

@Composable
private fun WidgetHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            provider = ImageProvider(R.drawable.ic_weekly_summary_notification),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
            modifier = GlanceModifier.size(18.dp),
        )
        Spacer(GlanceModifier.width(6.dp))
        Text(
            text = LocalContext.current.getString(R.string.widget_week_title),
            style =
                TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
        )
    }
}

@Composable
private fun ColumnScope.WeeklySummary(
    summary: WeeklyRunSummary,
    unit: DistanceUnit,
) {
    val context = LocalContext.current

    Text(
        text = formatDistance(summary.distanceMeters, unit),
        style =
            TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
            ),
    )
    Text(
        text = weeklyRunCountText(context, summary.runCount),
        style =
            TextStyle(
                color = GlanceTheme.colors.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
    )

    Spacer(GlanceModifier.defaultWeight())

    val recentRun = summary.mostRecentRun
    if (recentRun != null) {
        RecentRunRow(run = recentRun, unit = unit)
    }
}

@Composable
private fun RecentRunRow(
    run: Run,
    unit: DistanceUnit,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = LocalContext.current.getString(R.string.widget_recent_label),
            style =
                TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp,
                ),
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = formatRunDate(run.dateTimeUtc),
            style =
                TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp,
                ),
        )
        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = formatDistance(run.distanceMeters, unit),
            style =
                TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
        )
    }
}

@Composable
private fun ColumnScope.EmptyState() {
    val context = LocalContext.current
    Spacer(GlanceModifier.defaultWeight())
    Text(
        text = context.getString(R.string.widget_empty_title),
        style =
            TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
    )
    Spacer(GlanceModifier.height(2.dp))
    Text(
        text = context.getString(R.string.widget_empty_subtitle),
        style =
            TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
            ),
    )
    Spacer(GlanceModifier.defaultWeight())
}

internal fun weeklyRunCountText(
    context: Context,
    runCount: Int,
): String =
    if (runCount == 0) {
        context.getString(R.string.widget_no_run_this_week)
    } else {
        context.resources.getQuantityString(
            R.plurals.widget_runs_count,
            runCount,
            runCount,
        )
    }

private val previewRun =
    Run(
        id = RunId("preview-run"),
        duration = 32.minutes,
        dateTimeUtc = ZonedDateTime.of(2026, 7, 9, 7, 30, 0, 0, ZoneOffset.UTC),
        distanceMeters = 5_240,
        location = Location(lat = 37.5665, long = 126.9780),
        maxSpeedKmh = 15.2,
        totalElevationMeters = 38,
    )

@Composable
private fun WeeklyStatsPreviewCard(summary: WeeklyRunSummary) {
    GlanceTheme(colors = GyuRunGlanceColors) {
        WeeklyStatsContent(summary = summary, unit = DistanceUnit.KILOMETERS)
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 180, heightDp = 110)
@Composable
private fun WeeklyStatsEmptyPreview() {
    WeeklyStatsPreviewCard(
        WeeklyRunSummary(runCount = 0, distanceMeters = 0, mostRecentRun = null),
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 180, heightDp = 110)
@Composable
private fun WeeklyStatsThreeRunsPreview() {
    WeeklyStatsPreviewCard(
        WeeklyRunSummary(runCount = 3, distanceMeters = 15_500, mostRecentRun = previewRun),
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 180, heightDp = 110)
@Composable
private fun WeeklyStatsRestedThisWeekPreview() {
    WeeklyStatsPreviewCard(
        WeeklyRunSummary(runCount = 0, distanceMeters = 0, mostRecentRun = previewRun),
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 110)
@Composable
private fun WeeklyStatsWideCardPreview() {
    WeeklyStatsPreviewCard(
        WeeklyRunSummary(
            runCount = 12,
            distanceMeters = 126_400,
            mostRecentRun = previewRun.copy(distanceMeters = 21_097),
        ),
    )
}
