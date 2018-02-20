package com.clouddroid.pettypetscarehealth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clouddroid.pettypetscarehealth.model.Reminder
import org.jetbrains.anko.longToast

/**
 * Created by arkadiusz on 19.02.18
 */

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val args = intent?.getBundleExtra("bundleData")
        val passedReminder = args?.getParcelable("reminderObject") as Reminder
        context?.longToast(passedReminder.toString())
    }
}