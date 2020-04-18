package dev.csaba.assetlocationlogger.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.csaba.assetlocationlogger.R


class StagingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staging)

        val settingHelpText: TextView = findViewById(R.id.settingsHelp)
        settingHelpText.movementMethod = LinkMovementMethod.getInstance()

        val settingButton: Button = findViewById(R.id.settingsButton)
        settingButton.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }

        val privacyButton: Button = findViewById(R.id.privacyButton)
        privacyButton.setOnClickListener {
            openNewTabWindow(applicationContext.getString(R.string.privacy_policy_url), applicationContext)
        }

        val helpButton: Button = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            openNewTabWindow(applicationContext.getString(R.string.home_page_url), applicationContext)
        }
    }

    private fun openNewTabWindow(url: String?, context: Context) {
        val uris = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }
}
