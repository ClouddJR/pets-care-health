package com.clouddroid.petscarehealth

import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import android.preference.PreferenceManager
import com.clouddroid.petscarehealth.utils.AdsUtils
import com.clouddroid.petscarehealth.utils.NotificationsUtils
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Arkadiusz on 18.12.2017
 */
class PettyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        enableFirebaseOfflinePersistence()
        setUpDefaultSettings()
        setUpAds()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelForReminders()
        }
    }

    private fun enableFirebaseOfflinePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    private fun setUpDefaultSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    private fun setUpAds() {
        MobileAds.initialize(this, AdsUtils.appID)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelForReminders() {
        NotificationsUtils.createNotificationChannel(applicationContext)
    }
}