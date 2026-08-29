package com.gyugle.gyurun.reminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gyugle.gyurun.MainActivity
import com.gyugle.gyurun.R
import com.gyugle.gyurun.core.common.GyuRunNotification
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.DistanceUnit
import com.gyugle.gyurun.core.domain.run.WeeklyRunSummary
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.presentation.ui.formatDistance
import com.gyugle.gyurun.core.presentation.ui.hasNotificationPermission
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

internal class WeeklySummaryWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository,
    private val userSettingsRepository: UserSettingsRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val weekAgo = WeeklyRunSummary.weekAgo(ZonedDateTime.now())
        val weekRuns = runRepository.getRunsSince(weekAgo).first()
        val settings = userSettingsRepository.userSettings.first()
        val summary = WeeklyRunSummary.weekTotalsOnly(weekRuns)

        val context = applicationContext
        postNotification(
            title = context.getString(R.string.weekly_summary_notification_title),
            text = weeklySummaryText(context, summary, settings.distanceUnit),
        )
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun postNotification(
        title: String,
        text: String,
    ) {
        val context = applicationContext
        val notificationManager = NotificationManagerCompat.from(context)

        val channel =
            NotificationChannelCompat
                .Builder(
                    GyuRunNotification.WEEKLY_SUMMARY.channelId,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT,
                ).setName(context.getString(R.string.weekly_summary_notification_channel_name))
                .build()
        notificationManager.createNotificationChannel(channel)

        val activityIntent =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                PENDING_INTENT_REQUEST_CODE,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(context, GyuRunNotification.WEEKLY_SUMMARY.channelId)
                .setSmallIcon(R.drawable.ic_weekly_summary_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        if (context.hasNotificationPermission()) {
            notificationManager.notify(GyuRunNotification.WEEKLY_SUMMARY.id, notification)
        }
    }

    companion object {
        private const val PENDING_INTENT_REQUEST_CODE = 1
        private const val WORK_NAME = "weekly_summary_reminder"

        fun schedule(context: Context) {
            val request =
                PeriodicWorkRequestBuilder<WeeklySummaryWorker>(
                    repeatInterval = WeeklyRunSummary.DAYS_IN_WEEK,
                    repeatIntervalTimeUnit = TimeUnit.DAYS,
                ).setInitialDelay(WeeklyRunSummary.DAYS_IN_WEEK, TimeUnit.DAYS)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }
    }
}

internal fun weeklySummaryText(
    context: Context,
    summary: WeeklyRunSummary,
    unit: DistanceUnit,
): String =
    if (summary.runCount == 0) {
        context.getString(R.string.weekly_summary_notification_empty)
    } else {
        context.getString(
            R.string.weekly_summary_notification_text,
            summary.runCount,
            formatDistance(summary.distanceMeters, unit),
        )
    }