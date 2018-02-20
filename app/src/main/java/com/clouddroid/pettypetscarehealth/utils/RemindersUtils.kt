package com.clouddroid.pettypetscarehealth.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.receivers.ReminderReceiver
import org.jetbrains.anko.alarmManager
import java.util.*


/**
 * Created by arkadiusz on 19.02.18
 */

object RemindersUtils {

    fun addNewReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.alarmManager

        val intent = Intent(context, ReminderReceiver::class.java)
        val bundle = Bundle()
        bundle.putParcelable("reminderObject", reminder as Parcelable)
        intent.putExtra("bundleData", bundle)

        val alarmIntent = PendingIntent.getBroadcast(context, reminder.toString().hashCode(), intent, 0)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, reminder.year)
        calendar.set(Calendar.MONTH, reminder.month)
        calendar.set(Calendar.DAY_OF_MONTH, reminder.day)
        calendar.set(Calendar.HOUR_OF_DAY, reminder.hour)
        calendar.set(Calendar.MINUTE, reminder.minute)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }

    fun deleteReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.alarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, reminder.toString().hashCode(), intent, 0)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, reminder.year)
        calendar.set(Calendar.MONTH, reminder.month)
        calendar.set(Calendar.DAY_OF_MONTH, reminder.day)
        calendar.set(Calendar.HOUR_OF_DAY, reminder.hour)
        calendar.set(Calendar.MINUTE, reminder.minute)

        alarmManager.cancel(alarmIntent)
    }
}