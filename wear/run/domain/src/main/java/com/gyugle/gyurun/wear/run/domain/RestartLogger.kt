package com.gyugle.gyurun.wear.run.domain

/**
 * 끊겼을 때 사용하기 위함.
 */
fun interface RestartLogger {
    fun onRestart(
        pipe: String,
        cause: Throwable,
        attempt: Long,
    )
}
