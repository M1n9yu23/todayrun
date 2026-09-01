package com.gyugle.gyurun.wear.app.exercise

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.wear.ongoing.OngoingActivity
import com.gyugle.gyurun.wear.app.MainActivity
import com.gyugle.gyurun.wear.app.R
import com.gyugle.gyurun.wear.run.domain.RunningTracker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ExerciseService :
    Service(),
    KoinComponent {
    private val runningTracker: RunningTracker by inject()

    private val notificationManager by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (exerciseServiceCommandFor(intent?.action)) {
            ExerciseServiceCommand.START -> start()
            ExerciseServiceCommand.STOP -> stop()
            ExerciseServiceCommand.NONE -> Unit
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        runningTracker.setExerciseServiceRunning(false)
    }

    private fun start() {
        if (runningTracker.isExerciseServiceRunning.value) return
        runningTracker.setExerciseServiceRunning(true)

        createNotificationChannel()

        val activityPendingIntent = createActivityPendingIntent()

        val notificationBuilder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_run_notification)
                .setContentTitle(getString(R.string.wear_exercise_notification_title))
                .setContentText(getString(R.string.wear_exercise_notification_text))
                .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                .setContentIntent(activityPendingIntent)
                .setOngoing(true)

        val ongoingActivity =
            OngoingActivity
                .Builder(this, NOTIFICATION_ID, notificationBuilder)
                .setStaticIcon(R.drawable.ic_run_notification)
                .setTouchIntent(activityPendingIntent)
                .build()
        ongoingActivity.apply(this)

        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notificationBuilder.build(),
                healthServiceType(),
            )
        } catch (e: SecurityException) {
            Timber.e(e, "운동 포그라운드 서비스를 세우지 못했다 — 필요한 권한이 없다")
            failAndStop()
        } catch (e: IllegalStateException) {
            Timber.e(e, "운동 포그라운드 서비스를 세우지 못했다 — 지금은 앞에 세울 수 없다")
            failAndStop()
        }
    }

    private fun failAndStop() {
        runningTracker.reportExerciseServiceFailed()
        stop()
    }

    private fun stop() {
        runningTracker.setExerciseServiceRunning(false)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun healthServiceType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        } else {
            0
        }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannelCompat
                .Builder(
                    CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_LOW,
                ).setName(getString(R.string.wear_exercise_notification_channel_name))
                .build()
        notificationManager.createNotificationChannel(channel)
    }

    private fun createActivityPendingIntent(): PendingIntent {
        val activityIntent =
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        return PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_CODE,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val CHANNEL_ID = "active_run"
        private const val NOTIFICATION_ID = 1
        private const val PENDING_INTENT_REQUEST_CODE = 0

        fun createStartIntent(context: Context): Intent =
            Intent(context, ExerciseService::class.java).apply {
                action = ACTION_START_EXERCISE
            }

        fun createStopIntent(context: Context): Intent =
            Intent(context, ExerciseService::class.java).apply {
                action = ACTION_STOP_EXERCISE
            }
    }
}
