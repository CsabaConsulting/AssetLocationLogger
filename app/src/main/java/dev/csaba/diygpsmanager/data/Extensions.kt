package dev.csaba.diygpsmanager.data

import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager


fun getPreferenceString(preferences: SharedPreferences, name: String): String {
    return preferences.getString(name, "") ?: return ""
}

fun mapValueToInterval(intervals: IntArray, value: Int): Int {
    if (value < 0)
        return intervals.first()

    if (value >= intervals.size)
        return intervals.last()

    return intervals[value]
}

fun mapReportHistorySeekBarToMinutes(seekBarValue: Int): Int {
    val intervals = intArrayOf(10, 60, 1440, 2880, 4320, 7200, 14400)
    return mapValueToInterval(intervals, seekBarValue)
}

fun FragmentActivity.getSecondaryFirebaseConfiguration(): FirebaseProjectConfiguration {
    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    return FirebaseProjectConfiguration(
        getPreferenceString(preferences, "project_id"),
        getPreferenceString(preferences, "application_id"),
        getPreferenceString(preferences, "api_key"),
        preferences.getBoolean("auth_type", false),
        mapReportHistorySeekBarToMinutes(preferences.getInt("report_history", 0))
    )
}

fun FragmentActivity.hasAuthConfiguration(): Boolean {
    val configuration = this.getSecondaryFirebaseConfiguration()
    return configuration.projectId.isNotBlank() &&
            configuration.applicationId.isNotBlank() &&
            configuration.apiKey.isNotBlank()
}
