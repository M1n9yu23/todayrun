package com.gyugle.gyurun.wear.run.presentation

sealed interface TrackerAction {
    data object OnToggleRunClick : TrackerAction

    data object OnFinishRunClick : TrackerAction

    data object OnExercisePermissionClick : TrackerAction

    data class OnPermissionResult(
        val activityPermission: PermissionState,
        val heartRatePermission: PermissionState,
    ) : TrackerAction
}
