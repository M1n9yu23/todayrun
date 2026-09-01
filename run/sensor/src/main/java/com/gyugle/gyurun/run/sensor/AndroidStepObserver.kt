package com.gyugle.gyurun.run.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.gyugle.gyurun.run.domain.StepObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal class AndroidStepObserver(
    private val context: Context,
) : StepObserver {
    override fun observeSteps(): Flow<Int> {
        return callbackFlow {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close()
                return@callbackFlow
            }

            val sensorManager =
                context.getSystemService<SensorManager>() ?: run {
                    close()
                    return@callbackFlow
                }

            val stepSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: run {
                    close()
                    return@callbackFlow
                }

            val baseline = StepCountBaseline()

            val listener =
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        val totalStepsSinceBoot = event.values.firstOrNull()?.toInt() ?: return
                        trySend(baseline.stepsSinceStart(totalStepsSinceBoot))
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int,
                    ) = Unit
                }

            sensorManager.registerListener(
                listener,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL,
            )

            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
