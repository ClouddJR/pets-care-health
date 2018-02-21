package com.clouddroid.pettypetscarehealth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.utils.NotificationsUtils
import com.clouddroid.pettypetscarehealth.utils.RemindersUtils

/**
 * Created by arkadiusz on 19.02.18
 */

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val args = intent?.getBundleExtra("bundleData")
        val passedReminder = args?.getParcelable("reminderObject") as Reminder
        NotificationsUtils.createNotification(context, passedReminder)
        RemindersUtils.addNewReminder(context!!, passedReminder)
    }
}