package com.gyugle.gyurun.feature.stats.presentation.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.gyugle.gyurun.core.domain.run.WeeklyDistance
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.motion
import com.gyugle.gyurun.core.presentation.designsystem.spacing
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.formatDistance

private val ChartHeight = 200.dp
private const val BAR_WIDTH_RATIO = 0.55f
private val BarCornerRadius = 6.dp
private val LabelTopGap = 8.dp
private val BaselineThickness = 1.dp

internal fun chartMaxDistanceMeters(weeks: List<WeeklyDistance>): Int {
    val largest = weeks.maxOfOrNull { it.distanceMeters } ?: 0
    return if (largest > 0) largest else 1
}

internal fun barFraction(
    distanceMeters: Int,
    maxDistanceMeters: Int,
): Float = distanceMeters / maxDistanceMeters.toFloat()

internal fun barTop(
    plotHeight: Float,
    barHeight: Float,
): Float = plotHeight - barHeight

internal fun slotWidth(
    totalWidth: Float,
    weekCount: Int,
): Float = if (weekCount <= 0) 0f else totalWidth / weekCount

internal fun centeredLeft(
    slotLeft: Float,
    slotWidth: Float,
    itemWidth: Float,
): Float = slotLeft + (slotWidth - itemWidth) / 2f

@Composable
internal fun WeeklyDistanceChart(
    weeks: List<WeeklyDistance>,
    modifier: Modifier = Modifier,
) {
    val barColor = MaterialTheme.colorScheme.primary
    val baselineColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = MaterialTheme.typography.labelSmall
    val textMeasurer = rememberTextMeasurer()
    val motion = MaterialTheme.motion

    val maxDistanceMeters = remember(weeks) { chartMaxDistanceMeters(weeks) }

    val growProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        growProgress.animateTo(targetValue = 1f, animationSpec = motion.growSpec)
    }

    val chartDescription = weeklyChartContentDescription(weeks)

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ChartHeight)
                .semantics { contentDescription = chartDescription },
    ) {
        val cornerRadiusPx = BarCornerRadius.toPx()
        val labelGapPx = LabelTopGap.toPx()
        val labelHeightPx =
            textMeasurer
                .measure("0/0", labelStyle)
                .size.height
                .toFloat()
        val plotHeight = size.height - labelHeightPx - labelGapPx
        val slotWidthPx = slotWidth(size.width, weeks.size)
        val barWidth = slotWidthPx * BAR_WIDTH_RATIO

        drawLine(
            color = baselineColor,
            start = Offset(0f, plotHeight),
            end = Offset(size.width, plotHeight),
            strokeWidth = BaselineThickness.toPx(),
        )

        weeks.forEachIndexed { index, week ->
            val slotLeft = index * slotWidthPx
            val barLeft = centeredLeft(slotLeft, slotWidthPx, barWidth)
            val fraction = barFraction(week.distanceMeters, maxDistanceMeters)
            val barHeight = plotHeight * fraction * growProgress.value

            if (barHeight > 0f) {
                val effectiveCorner = cornerRadiusPx.coerceAtMost(barHeight / 2f)
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(barLeft, barTop(plotHeight, barHeight)),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(effectiveCorner, effectiveCorner),
                )
            }

            val label = "${week.weekStart.monthValue}/${week.weekStart.dayOfMonth}"
            val measuredLabel = textMeasurer.measure(label, labelStyle)
            drawText(
                textLayoutResult = measuredLabel,
                color = labelColor,
                topLeft =
                    Offset(
                        x = centeredLeft(slotLeft, slotWidthPx, measuredLabel.size.width.toFloat()),
                        y = plotHeight + labelGapPx,
                    ),
            )
        }
    }
}

@Composable
private fun weeklyChartContentDescription(weeks: List<WeeklyDistance>): String {
    val unit = LocalDistanceUnit.current
    val peakMeters = weeks.maxOfOrNull { it.distanceMeters } ?: 0
    val latestMeters = weeks.lastOrNull()?.distanceMeters ?: 0
    return stringResource(
        R.string.stats_chart_content_description,
        weeks.size,
        formatDistance(peakMeters, unit),
        formatDistance(latestMeters, unit),
    )
}

@PreviewLightDark
@Composable
private fun WeeklyDistanceChartPreview() {
    GyuRunTheme {
        Surface {
            WeeklyDistanceChart(
                weeks = previewWeeklyDistances(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.large),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WeeklyDistanceChartAllZeroPreview() {
    GyuRunTheme {
        Surface {
            WeeklyDistanceChart(
                weeks = previewWeeklyDistances(List(8) { 0 }),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.large),
            )
        }
    }
}
