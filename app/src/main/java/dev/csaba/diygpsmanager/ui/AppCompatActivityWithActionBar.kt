package dev.csaba.diygpsmanager.ui

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.data.hasConfiguration

abstract class AppCompatActivityWithActionBar : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.settings_menu_button) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.assets_menu_button) {
            if (!this.hasConfiguration()) {
                Toast.makeText(this,
                    getString(R.string.uncofigured_firestore),
                    Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}