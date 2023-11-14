package com.riviem.sunalarm.core.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.riviem.sunalarm.NotificationReceiver
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel

const val ACTION_DISMISS_ALARM = "ACTION_DISMISS_ALARM"

fun createDismissSoundNotification(context: Context, alarmUIModel: AlarmUIModel) {
    val channelId = "dismiss_sound_alarm_channel_id"
    val channel = NotificationChannel(
        channelId,
        context.getString(R.string.dismiss_sound_alarm_notification_name),
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = context.getString(R.string.dismiss_next_sound_alarm)
    }

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val dismissIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra(Constants.CREATED_TIMESTAMP_ID, alarmUIModel.createdTimestamp + 1)
        action = ACTION_DISMISS_ALARM
    }

    val dismissPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, Constants.DISMISS_SOUND_ALARM_NOTIFICATION_ID, dismissIntent, PendingIntent.FLAG_MUTABLE)

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(context.getString(R.string.dismiss_sound_alarm_notification_name))
        .setContentText(
            context.getString(
                R.string.next_sound_alarm_in_minutes,
                alarmUIModel.minutesUntilSoundAlarm
            )
        )
        .setSmallIcon(R.drawable.ic_launcher_background)
        .addAction(R.drawable.ic_launcher_background,
            context.getString(R.string.dismiss), dismissPendingIntent)
        .build()

    notificationManager.notify(Constants.DISMISS_SOUND_ALARM_NOTIFICATION_ID, notification)
}