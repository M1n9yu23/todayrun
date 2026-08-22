package com.gyugle.gyurun.core.presentation.ui

import com.gyugle.gyurun.core.domain.run.DistanceUnit
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration

fun formatDistance(
    distanceMeters: Int,
    unit: DistanceUnit,
): String =
    String.format(Locale.getDefault(), "%.2f %s", distanceMeters / unit.metersPerUnit, unit.symbol)

fun formatDuration(duration: Duration): String =
    duration.toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }

fun formatPace(
    pacePerKilometer: Duration,
    unit: DistanceUnit,
): String {
    if (pacePerKilometer == Duration.ZERO) return "-"
    return formatPaceFromSecondsPerKm(pacePerKilometer.inWholeSeconds.toDouble(), unit)
}

fun formatPace(
    distanceMeters: Int,
    duration: Duration,
    unit: DistanceUnit,
): String {
    if (distanceMeters <= 0) return "-"
    val secondsPerKm = duration.inWholeSeconds / (distanceMeters / 1000.0)
    return formatPaceFromSecondsPerKm(secondsPerKm, unit)
}

private fun formatPaceFromSecondsPerKm(
    secondsPerKm: Double,
    unit: DistanceUnit,
): String {
    val secondsPerUnit = secondsPerKm * (unit.metersPerUnit / 1000.0)
    val minutes = (secondsPerUnit / 60).toInt()
    val seconds = (secondsPerUnit % 60).toInt()
    return String.format(Locale.getDefault(), "%d:%02d /%s", minutes, seconds, unit.symbol)
}

fun formatSpeed(
    speedKmh: Double,
    unit: DistanceUnit,
): String {
    val perHour = speedKmh / (unit.metersPerUnit / 1000.0)
    return String.format(Locale.getDefault(), "%.1f %s", perHour, unit.speedLabel)
}

fun formatElevation(elevationMeters: Int): String =
    String.format(Locale.getDefault(), "%d m", elevationMeters)

fun formatSteps(steps: Int): String = String.format(Locale.getDefault(), "%,d", steps)

fun formatRunDate(
    dateTimeUtc: ZonedDateTime,
    style: FormatStyle = FormatStyle.MEDIUM,
): String =
    dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
        .format(
            DateTimeFormatter
                .ofLocalizedDate(style)
                .withLocale(Locale.getDefault()),
        )

fun formatTemperature(celsius: Double): String =
    String.format(Locale.getDefault(), "%.0f°", celsius)

fun formatHumidity(percent: Int): String = String.format(Locale.getDefault(), "%d%%", percent)