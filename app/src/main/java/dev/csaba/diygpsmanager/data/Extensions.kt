package dev.csaba.diygpsmanager.data

import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager


fun getPreferenceString(preferences: SharedPreferences, name: String): String {
    return preferences.getString(name, "") ?: return ""
}

fun FragmentActivity.getSecondaryFirebaseConfiguration(): FirebaseProjectConfiguration {
    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    return FirebaseProjectConfiguration(
        getPreferenceString(preferences, "project_id"),
        getPreferenceString(preferences, "application_id"),
        getPreferenceString(preferences, "api_key"),
        preferences.getBoolean("auth_type", false)
    )
}

fun FragmentActivity.hasConfiguration(): Boolean {
    val configuration = this.getSecondaryFirebaseConfiguration()
    return configuration.projectId.isNotBlank() &&
            configuration.applicationId.isNotBlank() &&
            configuration.apiKey.isNotBlank()
}
