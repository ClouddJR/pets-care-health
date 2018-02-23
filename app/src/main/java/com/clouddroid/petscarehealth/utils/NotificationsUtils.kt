package com.clouddroid.petscarehealth.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.activities.MainActivity
import com.clouddroid.petscarehealth.model.Reminder
import org.jetbrains.anko.notificationManager


/**
 * Created by arkadiusz on 21.02.18
 */

object NotificationsUtils {

    private const val channelID = "channel_1"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context?) {
        val notificationManager = context?.notificationManager
        val name = context?.getString(R.string.notification_channel_name)
        val description = context?.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.YELLOW
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)

        notificationManager?.createNotificationChannel(channel)
    }

    fun createNotification(context: Context?, reminder: Reminder) {
        val mBuilder = NotificationCompat.Builder(context!!, channelID)
                .setSmallIcon(R.drawable.ic_pets)
                .setContentTitle(reminder.animalName)
                .setContentText(reminder.text)
        val resultIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pendingIntent)
        val notificationManager = context.notificationManager
        notificationManager.notify(reminder.key.hashCode(), mBuilder.build())
    }
}