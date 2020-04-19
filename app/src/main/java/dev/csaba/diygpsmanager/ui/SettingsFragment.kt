package dev.csaba.diygpsmanager.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.data.hasConfiguration


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    fun openBrowserWindow(url: String?, context: Context?) {
        val uris = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intent.putExtras(bundle)
        context?.startActivity(intent)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == context?.getString(R.string.settings_help_key)) {
            openBrowserWindow(context?.getString(R.string.home_page_url), context)
            return true
        } else if (preference.key == context?.getString(R.string.connect_key)) {
            if (!requireActivity().hasConfiguration()) {
                Toast.makeText(context,
                    requireContext().getString(R.string.uncofigured_firestore),
                    Toast.LENGTH_SHORT).show()
            } else {
                val mainPage = Intent(context, MainActivity::class.java)
                startActivity(mainPage)
            }
        }
        return false
    }
}
