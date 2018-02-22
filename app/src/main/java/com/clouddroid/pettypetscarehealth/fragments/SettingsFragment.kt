package com.clouddroid.pettypetscarehealth.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.fragments.SettingsFragment.Keys.heightPreferencesKey
import com.clouddroid.pettypetscarehealth.fragments.SettingsFragment.Keys.weightPreferencesKey
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by arkadiusz on 22.02.18
 */

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    object Keys {
        const val heightPreferencesKey = "pref_heightUnitType"
        const val weightPreferencesKey = "pref_weightUnitType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        setSummaries()
    }

    private fun setSummaries() {
        findPreference(heightPreferencesKey).summary = defaultSharedPreferences.getString(heightPreferencesKey, "")
        findPreference(weightPreferencesKey).summary = defaultSharedPreferences.getString(weightPreferencesKey, "")
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key?.equals(heightPreferencesKey) == true) {
            val connectionPref = findPreference(heightPreferencesKey)
            connectionPref.summary = sharedPreferences?.getString(heightPreferencesKey, "")
        }

        if (key?.equals(weightPreferencesKey) == true) {
            val connectionPref = findPreference(weightPreferencesKey)
            connectionPref.summary = sharedPreferences?.getString(weightPreferencesKey, "")
        }
    }
}