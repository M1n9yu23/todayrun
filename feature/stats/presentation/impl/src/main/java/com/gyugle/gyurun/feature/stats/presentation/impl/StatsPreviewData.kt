package com.gyugle.gyurun.feature.stats.presentation.impl

import com.gyugle.gyurun.core.domain.run.RunStatistics
import com.gyugle.gyurun.core.domain.run.WeeklyDistance
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

private val PreviewFirstWeek: LocalDate = LocalDate.of(2026, 5, 4)

private val PreviewWeeklyMeters = listOf(12_400, 8_100, 0, 15_600, 9_800, 18_200, 6_500, 21_300)

internal val previewStatistics: RunStatistics =
    RunStatistics(
        totalRuns = 12,
        totalDistanceMeters = 84_300,
        totalDuration = (7 * 3600 + 45 * 60).seconds,
        totalElevationMeters = 640,
    )

internal fun previewWeeklyDistances(meters: List<Int> = PreviewWeeklyMeters): List<WeeklyDistance> =
    meters.mapIndexed { index, distanceMeters ->
        WeeklyDistance(
            weekStart = PreviewFirstWeek.plusWeeks(index.toLong()),
            distanceMeters = distanceMeters,
        )
    }
