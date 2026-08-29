package com.gyugle.gyurun.wear.run.data

import android.annotation.SuppressLint
import androidx.health.services.client.data.ExerciseTrackedStatus
import com.gyugle.gyurun.wear.run.domain.ExerciseError

@SuppressLint("RestrictedApi")
internal fun exerciseErrorFor(trackedStatus: Int): ExerciseError? =
    when (trackedStatus) {
        ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> null
        ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> ExerciseError.ONGOING_OWN_EXERCISE
        ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> ExerciseError.ONGOING_OTHER_EXERCISE
        else -> ExerciseError.UNKNOWN
    }