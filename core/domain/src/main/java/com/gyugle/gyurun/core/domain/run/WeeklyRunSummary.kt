package com.gyugle.gyurun.core.domain.run

import java.time.ZonedDateTime

data class WeeklyRunSummary(
    val runCount: Int,
    val distanceMeters: Int,
    val mostRecentRun: Run?,
) {
    companion object {
        const val DAYS_IN_WEEK = 7L

        fun weekAgo(now: ZonedDateTime): ZonedDateTime = now.minusDays(DAYS_IN_WEEK)

        fun of(
            runs: List<Run>,
            now: ZonedDateTime,
        ): WeeklyRunSummary =
            ofWeekRuns(
                weekRuns = runs.filter { it.dateTimeUtc.isAfter(weekAgo(now)) },
                mostRecentRun = runs.maxByOrNull { it.dateTimeUtc },
            )

        fun weekTotalsOnly(weekRuns: List<Run>): WeeklyRunSummary =
            ofWeekRuns(weekRuns, mostRecentRun = null)

        fun ofWeekRuns(
            weekRuns: List<Run>,
            mostRecentRun: Run?,
        ): WeeklyRunSummary =
            WeeklyRunSummary(
                runCount = weekRuns.size,
                distanceMeters = weekRuns.sumOf { it.distanceMeters },
                mostRecentRun = mostRecentRun,
            )
    }
}