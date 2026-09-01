package com.gyugle.gyurun.core.common

enum class GyuRunNotification(
    val id: Int,
    val channelId: String,
) {
    ACTIVE_RUN(id = 1, channelId = "active_run"),

    WEEKLY_SUMMARY(id = 2, channelId = "weekly_summary"),
}
