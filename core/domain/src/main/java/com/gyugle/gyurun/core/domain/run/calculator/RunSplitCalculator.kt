package com.gyugle.gyurun.core.domain.run.calculator

import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import com.gyugle.gyurun.core.domain.run.Split
import kotlin.math.roundToInt
import kotlin.time.Duration

object RunSplitCalculator {
    private const val SPLIT_DISTANCE_METERS = 1_000.0

    fun calculateSplits(route: List<List<LocationTimestamp>>): List<Split> {
        val splits = mutableListOf<Split>()
        var runningDistance = 0.0
        var runningDuration = Duration.ZERO
        var runningElevation = 0.0

        route.forEach { segment ->
            segment.zipWithNext().forEach { (from, to) ->
                var distance =
                    from.location.location
                        .distanceTo(to.location.location)
                        .toDouble()
                var duration = to.durationTimestamp - from.durationTimestamp
                var elevation =
                    (to.location.altitude - from.location.altitude).coerceAtLeast(0.0)

                while (runningDistance + distance >= SPLIT_DISTANCE_METERS) {
                    val distanceToMark = SPLIT_DISTANCE_METERS - runningDistance
                    val fraction = distanceToMark / distance
                    val durationToMark = duration * fraction
                    val elevationToMark = elevation * fraction

                    splits +=
                        Split(
                            distanceMeters = SPLIT_DISTANCE_METERS.toInt(),
                            duration = runningDuration + durationToMark,
                            elevationGainMeters =
                                (runningElevation + elevationToMark).roundToInt(),
                        )

                    distance -= distanceToMark
                    duration -= durationToMark
                    elevation -= elevationToMark
                    runningDistance = 0.0
                    runningDuration = Duration.ZERO
                    runningElevation = 0.0
                }

                runningDistance += distance
                runningDuration += duration
                runningElevation += elevation
            }
        }

        if (runningDistance > 0.0) {
            splits +=
                Split(
                    distanceMeters = runningDistance.roundToInt(),
                    duration = runningDuration,
                    elevationGainMeters = runningElevation.roundToInt(),
                )
        }

        return splits
    }
}