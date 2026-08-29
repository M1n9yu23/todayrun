package com.gyugle.gyurun.wear.run.presentation

import android.Manifest
import android.os.Build
import com.gyugle.gyurun.wear.run.domain.heartRatePermissionFor

internal const val ACTIVITY_PERMISSION = Manifest.permission.ACTIVITY_RECOGNITION

internal fun heartRatePermission(sdkInt: Int = Build.VERSION.SDK_INT): String =
    heartRatePermissionFor(sdkInt)

internal fun isAskable(state: PermissionState): Boolean =
    state == PermissionState.NOT_DETERMINED || state == PermissionState.DENIED

internal fun permissionStateAfterRequest(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
): PermissionState =
    when {
        isGranted -> PermissionState.GRANTED
        shouldShowRationale -> PermissionState.DENIED
        else -> PermissionState.PERMANENTLY_DENIED
    }

internal fun permissionStateWhenAsked(
    isGranted: Boolean,
    shouldShowRationale: Boolean,
): PermissionState =
    when {
        isGranted -> PermissionState.GRANTED
        shouldShowRationale -> PermissionState.DENIED
        else -> PermissionState.NOT_DETERMINED
    }