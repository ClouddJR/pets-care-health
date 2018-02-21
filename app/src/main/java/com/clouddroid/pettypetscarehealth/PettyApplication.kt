package com.clouddroid.pettypetscarehealth

import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import com.clouddroid.pettypetscarehealth.utils.NotificationsUtils
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Arkadiusz on 18.12.2017
 */
class PettyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        enableFirebaseOfflinePersistence()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelForReminders()
        }
    }

    private fun enableFirebaseOfflinePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelForReminders() {
        NotificationsUtils.createNotificationChannel(applicationContext)
    }
}