package com.gyugle.gyurun.core.data.run

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.repository.WeatherBackfillScheduler
import java.util.concurrent.TimeUnit

internal class WorkManagerWeatherBackfillScheduler(
    context: Context,
) : WeatherBackfillScheduler {
    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleBackfill(runId: RunId) {
        val workRequest =
            OneTimeWorkRequestBuilder<WeatherBackfillWorker>()
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                ).setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.EXPONENTIAL,
                    backoffDelay = 30,
                    timeUnit = TimeUnit.SECONDS,
                ).setInputData(
                    workDataOf(WeatherBackfillWorker.RUN_ID_KEY to runId.value),
                ).build()

        workManager
            .enqueueUniqueWork(
                "${WEATHER_BACKFILL_WORK}_${runId.value}",
                ExistingWorkPolicy.KEEP,
                workRequest,
            ).await()
    }

    private companion object {
        const val WEATHER_BACKFILL_WORK = "weather_backfill"
    }
}