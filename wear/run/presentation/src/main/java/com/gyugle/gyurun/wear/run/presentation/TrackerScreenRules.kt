package com.gyugle.gyurun.wear.run.presentation

import android.os.Build

internal fun canFinishRun(state: TrackerState): Boolean = state.hasStartedRunning && (!state.isRunActive || !state.isTrackable)

internal fun heartRateValueRes(state: TrackerState): Int? =
    when {
        state.heartRatePermission == PermissionState.PERMANENTLY_DENIED -> R.string.wear_heart_rate_in_settings
        state.heartRatePermission != PermissionState.GRANTED -> R.string.wear_heart_rate_needs_permission
        !state.canTrackHeartRate -> R.string.wear_heart_rate_unsupported
        !state.isHeartRateAvailable -> R.string.wear_heart_rate_acquiring
        else -> null
    }

internal fun recordingBlockedRes(state: TrackerState): Int? =
    when (state.activityPermission) {
        PermissionState.GRANTED -> null
        PermissionState.PERMANENTLY_DENIED -> R.string.wear_recording_in_settings
        PermissionState.DENIED, PermissionState.NOT_DETERMINED -> R.string.wear_recording_needs_permission
    }

internal fun permissionToAsk(
    state: TrackerState,
    sdkInt: Int = Build.VERSION.SDK_INT,
): String? =
    when {
        isAskable(state.activityPermission) -> ACTIVITY_PERMISSION
        isAskable(state.heartRatePermission) -> heartRatePermission(sdkInt)
        else -> null
    }

internal fun exercisePermissionButtonRes(state: TrackerState): Int? =
    when {
        isAskable(state.activityPermission) -> R.string.wear_allow_recording
        isAskable(state.heartRatePermission) -> R.string.wear_allow_heart_rate
        else -> null
    }
