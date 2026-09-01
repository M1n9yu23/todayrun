package com.gyugle.gyurun.core.domain.run.usecase

import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunStatistics
import kotlin.time.Duration

class GetRunStatisticsUseCase {
    operator fun invoke(runs: List<Run>): RunStatistics =
        RunStatistics(
            totalRuns = runs.size,
            totalDistanceMeters = runs.sumOf { it.distanceMeters },
            totalDuration = runs.fold(Duration.ZERO) { total, run -> total + run.duration },
            totalElevationMeters = runs.sumOf { it.totalElevationMeters },
        )
}
