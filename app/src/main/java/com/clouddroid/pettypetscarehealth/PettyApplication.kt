package com.clouddroid.pettypetscarehealth

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Arkadiusz on 18.12.2017
 */
class PettyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}