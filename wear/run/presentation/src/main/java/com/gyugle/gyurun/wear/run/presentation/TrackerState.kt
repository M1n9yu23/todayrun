package com.gyugle.gyurun.wear.run.presentation

import kotlin.time.Duration

data class TrackerState(
    val elapsedDuration: Duration = Duration.ZERO,
    val distanceMeters: Int = 0,
    val heartRate: Int = 0,
    val canTrackHeartRate: Boolean = false,
    val isHeartRateAvailable: Boolean = true,
    val activityPermission: PermissionState = PermissionState.NOT_DETERMINED,
    val heartRatePermission: PermissionState = PermissionState.NOT_DETERMINED,
    val isRequestingPermission: Boolean = false,
    val isRunActive: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val isConnectedPhoneNearby: Boolean = false,
    val isTrackable: Boolean = false,
)