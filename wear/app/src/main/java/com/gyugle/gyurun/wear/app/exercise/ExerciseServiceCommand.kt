package com.gyugle.gyurun.wear.app.exercise

internal const val ACTION_START_EXERCISE = "ACTION_START"
internal const val ACTION_STOP_EXERCISE = "ACTION_STOP"

internal enum class ExerciseServiceCommand {
    START,
    STOP,
    NONE,
}

internal fun exerciseServiceCommandFor(
    isTracking: Boolean,
    isServiceRunning: Boolean,
): ExerciseServiceCommand =
    when {
        isTracking -> ExerciseServiceCommand.START
        isServiceRunning -> ExerciseServiceCommand.STOP
        else -> ExerciseServiceCommand.NONE
    }

internal fun exerciseServiceCommandFor(action: String?): ExerciseServiceCommand =
    when (action) {
        ACTION_START_EXERCISE -> ExerciseServiceCommand.START
        ACTION_STOP_EXERCISE -> ExerciseServiceCommand.STOP
        else -> ExerciseServiceCommand.NONE
    }
