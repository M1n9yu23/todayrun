package com.gyugle.gyurun.wear.run.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.wear.run.domain.ExerciseError
import com.gyugle.gyurun.wear.run.domain.ExerciseTracker
import com.gyugle.gyurun.wear.run.domain.heartRatePermissionFor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import kotlin.math.roundToInt

internal class HealthServicesExerciseTracker(
    private val context: Context,
) : ExerciseTracker {
    private val client = HealthServices.getClient(context).exerciseClient

    private val _isHeartRateAvailable = MutableStateFlow(true)
    override val isHeartRateAvailable: Flow<Boolean> = _isHeartRateAvailable.asStateFlow()

    override val heartRate: Flow<Int>
        get() =
            callbackFlow {
                val callback =
                    object : ExerciseUpdateCallback {
                        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                            val heartRates = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
                            val currentHeartRate = heartRates.firstOrNull()?.value
                            currentHeartRate?.let {
                                trySend(it.roundToInt())
                            }
                        }

                        override fun onAvailabilityChanged(
                            dataType: DataType<*, *>,
                            availability: Availability,
                        ) {
                            if (availability is DataTypeAvailability) {
                                _isHeartRateAvailable.value =
                                    availability == DataTypeAvailability.AVAILABLE
                            }
                        }

                        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) = Unit

                        override fun onRegistered() = Unit

                        override fun onRegistrationFailed(throwable: Throwable) {
                            Timber.e(throwable, "운동 업데이트 콜백 등록 실패")
                        }
                    }

                client.setUpdateCallback(callback)

                awaitClose {
                    client.clearUpdateCallbackAsync(callback)
                    _isHeartRateAvailable.value = true
                }
            }

    override suspend fun isHeartRateTrackingSupported(): Boolean {
        if (!hasHeartRatePermission()) {
            return false
        }
        return try {
            val capabilities = client.getCapabilities()
            val supportedDataTypes =
                capabilities
                    .typeToCapabilities[ExerciseType.RUNNING]
                    ?.supportedDataTypes
                    ?: emptySet()
            DataType.HEART_RATE_BPM in supportedDataTypes
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "심박 지원 여부 조회 실패")
            false
        }
    }

    override suspend fun prepareExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val activeExerciseInfo = getActiveExerciseInfo()
        if (activeExerciseInfo is Result.Error) {
            return activeExerciseInfo
        }

        val config =
            WarmUpConfig(
                exerciseType = ExerciseType.RUNNING,
                dataTypes = setOf(DataType.HEART_RATE_BPM),
            )
        return try {
            client.prepareExercise(config)
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Timber.e(e, "운동 준비 실패")
            Result.Error(ExerciseError.UNKNOWN)
        }
    }

    override suspend fun startExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val activeExerciseInfo = getActiveExerciseInfo()
        if (activeExerciseInfo is Result.Error) {
            return activeExerciseInfo
        }

        val config =
            ExerciseConfig
                .builder(ExerciseType.RUNNING)
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .setIsAutoPauseAndResumeEnabled(false)
                .build()
        return try {
            client.startExercise(config)
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Timber.e(e, "운동 시작 실패")
            Result.Error(ExerciseError.UNKNOWN)
        }
    }

    override suspend fun resumeExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val activeExerciseInfo = getActiveExerciseInfo()
        if (activeExerciseInfo is Result.Error && activeExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return activeExerciseInfo
        }

        return try {
            client.resumeExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Timber.e(e, "운동 재개 실패")
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    override suspend fun pauseExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val activeExerciseInfo = getActiveExerciseInfo()
        if (activeExerciseInfo is Result.Error && activeExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return activeExerciseInfo
        }

        return try {
            client.pauseExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Timber.e(e, "운동 일시정지 실패")
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    override suspend fun stopExercise(): EmptyResult<ExerciseError> {
        if (!isHeartRateTrackingSupported()) {
            return Result.Error(ExerciseError.TRACKING_NOT_SUPPORTED)
        }

        val activeExerciseInfo = getActiveExerciseInfo()
        if (activeExerciseInfo is Result.Error && activeExerciseInfo.error == ExerciseError.ONGOING_OTHER_EXERCISE) {
            return activeExerciseInfo
        }

        return try {
            client.endExercise()
            Result.Success(Unit)
        } catch (e: HealthServicesException) {
            Timber.e(e, "운동 종료 실패")
            Result.Error(ExerciseError.EXERCISE_ALREADY_ENDED)
        }
    }

    @SuppressLint("RestrictedApi")
    private suspend fun getActiveExerciseInfo(): EmptyResult<ExerciseError> {
        val info = client.getCurrentExerciseInfo()
        val error = exerciseErrorFor(info.exerciseTrackedStatus)
        return if (error == null) Result.Success(Unit) else Result.Error(error)
    }

    private fun hasHeartRatePermission(): Boolean {
        val permission = heartRatePermissionFor(Build.VERSION.SDK_INT)
        return ContextCompat.checkSelfPermission(
            context,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
