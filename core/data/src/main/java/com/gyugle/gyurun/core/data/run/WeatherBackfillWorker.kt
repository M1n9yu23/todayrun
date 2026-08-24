package com.gyugle.gyurun.core.data.run

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.domain.weather.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import com.gyugle.gyurun.core.common.Result as DomainResult

internal class WeatherBackfillWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository,
    private val weatherRepository: WeatherRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): ListenableWorker.Result {
        if (runAttemptCount >= MAX_RETRIES) {
            Timber.w("날씨 백필 포기: 재시도 %d회를 넘겼다", runAttemptCount)
            return ListenableWorker.Result.failure()
        }

        val runId =
            inputData.getString(RUN_ID_KEY)
                ?: return ListenableWorker.Result.failure()

        val run = runRepository.getRun(RunId(runId)).first()
        if (run == null) {
            Timber.i("날씨 백필 건너뜀: 러닝 %s 이(가) 사라졌다", runId)
            return ListenableWorker.Result.success()
        }
        if (run.weather != null) {
            Timber.i("날씨 백필 건너뜀: 러닝 %s 은(는) 이미 날씨가 채워졌다", runId)
            return ListenableWorker.Result.success()
        }

        return when (val result = weatherRepository.getCurrentWeather(run.location)) {
            is DomainResult.Success -> {
                runRepository.updateWeather(RunId(runId), result.data)
                Timber.i("날씨 백필 성공: 러닝 %s", runId)
                ListenableWorker.Result.success()
            }

            is DomainResult.Error -> {
                Timber.w("날씨 백필 실패(%s): 러닝 %s", result.error, runId)
                result.error.toWorkerResult()
            }
        }
    }

    companion object {
        const val RUN_ID_KEY = "run_id"
        private const val MAX_RETRIES = 5
    }
}

internal fun DataError.Network.toWorkerResult(): ListenableWorker.Result =
    when (this) {
        DataError.Network.NO_INTERNET,
        DataError.Network.REQUEST_TIMEOUT,
        DataError.Network.SERVER_ERROR,
        DataError.Network.TOO_MANY_REQUESTS,
            -> ListenableWorker.Result.retry()

        else -> ListenableWorker.Result.failure()
    }