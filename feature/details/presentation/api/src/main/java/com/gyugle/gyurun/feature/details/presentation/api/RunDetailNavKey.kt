package com.gyugle.gyurun.feature.details.presentation.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RunDetailNavKey(
    val runId: String,
) : NavKey
