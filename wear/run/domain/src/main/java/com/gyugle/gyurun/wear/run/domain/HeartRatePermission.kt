package com.gyugle.gyurun.wear.run.domain

const val READ_HEART_RATE_PERMISSION = "android.permission.health.READ_HEART_RATE"
const val BODY_SENSORS_PERMISSION = "android.permission.BODY_SENSORS"

// 도메인 모듈이라서 버전코드 X
private const val ANDROID_16_SDK_INT = 36

fun heartRatePermissionFor(sdkInt: Int): String =
    if (sdkInt >= ANDROID_16_SDK_INT) {
        READ_HEART_RATE_PERMISSION
    } else {
        BODY_SENSORS_PERMISSION
    }