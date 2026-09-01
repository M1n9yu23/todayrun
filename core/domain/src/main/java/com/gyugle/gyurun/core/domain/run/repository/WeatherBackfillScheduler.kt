package com.gyugle.gyurun.core.domain.run.repository

import com.gyugle.gyurun.core.domain.run.RunId

/**
 * 러닝 저장하는 순간에 이터넷이 없을 수 있는 상황을 위한 repo
 */
interface WeatherBackfillScheduler {
    suspend fun scheduleBackfill(runId: RunId)
}
