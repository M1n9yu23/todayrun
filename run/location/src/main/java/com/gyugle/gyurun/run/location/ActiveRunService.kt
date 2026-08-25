package com.gyugle.gyurun.run.location

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.gyugle.gyurun.core.common.GyuRunNotification

class ActiveRunService : Service() {
    private val notificationManager by lazy {
        NotificationManagerCompat.from(this)
    }

    private val baseNotification by lazy {
        NotificationCompat
            .Builder(this, GyuRunNotification.ACTIVE_RUN.channelId)
            .setSmallIcon(R.drawable.ic_run_notification)
            .setContentTitle(getString(R.string.active_run_notification_title))
            .setContentText(getString(R.string.active_run_notification_text))
            .setOngoing(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_START -> start(intent.getStringExtra(EXTRA_ACTIVITY_CLASS))
            ACTION_STOP -> stop()
        }
        return START_REDELIVER_INTENT
    }

    private fun start(activityClassName: String?) {
        if (isServiceActive) return
        if (!hasLocationPermission()) {
            stopSelf()
            return
        }
        isServiceActive = true

        createNotificationChannel()

        val notification =
            baseNotification
                .setContentIntent(createActivityPendingIntent(activityClassName))
                .build()

        ServiceCompat.startForeground(
            this,
            GyuRunNotification.ACTIVE_RUN.id,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
        )
    }

    private fun stop() {
        isServiceActive = false
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        val coarseGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannelCompat
                .Builder(
                    GyuRunNotification.ACTIVE_RUN.channelId,
                    NotificationManagerCompat.IMPORTANCE_LOW,
                ).setName(getString(R.string.active_run_notification_channel_name))
                .build()
        notificationManager.createNotificationChannel(channel)
    }

    private fun createActivityPendingIntent(activityClassName: String?): PendingIntent? {
        if (activityClassName == null) return null

        val activityIntent =
            Intent(this, Class.forName(activityClassName)).apply {
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
        var isServiceActive = false
            private set

        private const val PENDING_INTENT_REQUEST_CODE = 0

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        fun createStartIntent(
            context: Context,
            activityClass: Class<*>,
        ): Intent =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }

        fun createStopIntent(context: Context): Intent =
            Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
    }
}