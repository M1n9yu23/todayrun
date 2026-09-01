package com.gyugle.gyurun.connectivity.data.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
internal sealed interface MessagingActionDto {
    @Serializable
    @SerialName("start_or_resume")
    data object StartOrResume : MessagingActionDto

    @Serializable
    @SerialName("pause")
    data object Pause : MessagingActionDto

    @Serializable
    @SerialName("finish")
    data object Finish : MessagingActionDto

    @Serializable
    @SerialName("connection_request")
    data object ConnectionRequest : MessagingActionDto

    @Serializable
    @SerialName("trackable")
    data object Trackable : MessagingActionDto

    @Serializable
    @SerialName("untrackable")
    data object Untrackable : MessagingActionDto

    @Serializable
    @SerialName("heart_rate")
    data class HeartRateUpdate(
        val heartRate: Int,
    ) : MessagingActionDto

    @Serializable
    @SerialName("step_count")
    data class StepCountUpdate(
        val stepCount: Int,
    ) : MessagingActionDto

    @Serializable
    @SerialName("distance")
    data class DistanceUpdate(
        val distanceMeters: Int,
    ) : MessagingActionDto

    @Serializable
    @SerialName("elapsed_time")
    data class TimeUpdate(
        val elapsedDuration: Duration,
    ) : MessagingActionDto
}
