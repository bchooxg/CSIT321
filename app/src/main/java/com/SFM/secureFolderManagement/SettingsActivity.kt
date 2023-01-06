package com.SFM.secureFolderManagement

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.SFM.secureFolderManagement.activities.PasswordActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val preference: Preference? = findPreference("change_pin")
        preference?.setOnPreferenceClickListener {
            context?.let { it1 -> getDefaultSharedPreferences(it1).edit().putBoolean("isPinSet", false).apply() }
            val intent = Intent(context, PasswordActivity::class.java)
            startActivity(intent)
            true
        }

    }
}