package com.clouddroid.petscarehealth.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.clouddroid.petscarehealth.fragments.SettingsFragment


/**
 * Created by arkadiusz on 22.02.18
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}