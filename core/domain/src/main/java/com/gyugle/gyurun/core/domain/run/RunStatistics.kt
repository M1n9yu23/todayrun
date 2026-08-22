package com.gyugle.gyurun.core.domain.run

import kotlin.time.Duration
import kotlin.time.DurationUnit

data class RunStatistics(
    val totalRuns: Int,
    val totalDistanceMeters: Int,
    val totalDuration: Duration,
    val totalElevationMeters: Int,
) {
    val averageSpeedKmh: Double
        get() =
            if (totalDuration == Duration.ZERO) {
                0.0
            } else {
                (totalDistanceMeters / 1000.0) / totalDuration.toDouble(DurationUnit.HOURS)
            }
}
