package com.gyugle.gyurun.core.domain.run.calculator

import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.WeeklyDistance
import java.time.LocalDate
import java.time.ZoneId

object WeeklyDistanceCalculator {
    const val WEEKS_SHOWN = 8

    fun calculate(runs: List<Run>): List<WeeklyDistance> {
        if (runs.isEmpty()) {
            return emptyList()
        }

        val distanceByWeek =
            runs
                .groupBy { run -> run.weekStart() }
                .mapValues { (_, weekRuns) -> weekRuns.sumOf { it.distanceMeters } }

        val latestWeek = distanceByWeek.keys.max()

        return (WEEKS_SHOWN - 1 downTo 0).map { weeksAgo ->
            val weekStart = latestWeek.minusWeeks(weeksAgo.toLong())
            WeeklyDistance(
                weekStart = weekStart,
                distanceMeters = distanceByWeek[weekStart] ?: 0,
            )
        }
    }

    private fun Run.weekStart(): LocalDate {
        val localDate =
            dateTimeUtc
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate()
        return localDate.minusDays((localDate.dayOfWeek.value - 1).toLong())
    }
}
