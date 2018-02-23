package com.clouddroid.petscarehealth.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.clouddroid.petscarehealth.model.Reminder
import com.clouddroid.petscarehealth.receivers.ReminderReceiver
import org.jetbrains.anko.alarmManager
import java.util.*


/**
 * Created by arkadiusz on 19.02.18
 */

object RemindersUtils {

    fun addNewReminder(context: Context, reminder: Reminder): Boolean {
        val alarmManager = context.alarmManager
        val bundle = prepareBundle(reminder)
        val intent = prepareIntent(context, bundle)
        val alarmIntent = PendingIntent.getBroadcast(context, reminder.key.hashCode(), intent, 0)

        if (wasSingleEventInThePast(reminder)) {
            return false
        }

        val calendar = updateReminderCalendar(reminder)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        return true
    }

    fun deleteReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.alarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, reminder.key.hashCode(), intent, 0)

        alarmManager.cancel(alarmIntent)
    }

    private fun prepareBundle(reminder: Reminder): Bundle {
        val bundle = Bundle()
        bundle.putParcelable("reminderObject", reminder as Parcelable)
        return bundle
    }

    private fun prepareIntent(context: Context, bundle: Bundle): Intent {
        val intent = Intent(context, ReminderReceiver::class.java)
        intent.putExtra("bundleData", bundle)
        return intent
    }

    private fun wasSingleEventInThePast(reminder: Reminder): Boolean {
        val reminderCalendar = generateCalendar(reminder)
        val todayCalendar = generateTodayCalendar()

        return (reminderCalendar.before(todayCalendar) && reminder.numberIntervals == 0)
    }

    private fun updateReminderCalendar(reminder: Reminder): Calendar {
        val reminderCalendar = generateCalendar(reminder)
        val todayCalendar = generateTodayCalendar()

        while (reminderCalendar.before(todayCalendar) && reminder.numberIntervals != 0) {
            when (reminder.typeInterval) {
                "days" -> reminderCalendar.add(Calendar.DAY_OF_MONTH, reminder.numberIntervals)
                "weeks" -> reminderCalendar.add(Calendar.WEEK_OF_YEAR, reminder.numberIntervals)
                "months" -> reminderCalendar.add(Calendar.MONTH, reminder.numberIntervals)
            }
        }
        return reminderCalendar
    }

    private fun generateCalendar(reminder: Reminder): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, reminder.year)
        calendar.set(Calendar.MONTH, reminder.month)
        calendar.set(Calendar.DAY_OF_MONTH, reminder.day)
        calendar.set(Calendar.HOUR_OF_DAY, reminder.hour)
        calendar.set(Calendar.MINUTE, reminder.minute)
        calendar.set(Calendar.SECOND, 59)
        return calendar
    }

    private fun generateTodayCalendar(): Calendar {
        return Calendar.getInstance()
    }
}