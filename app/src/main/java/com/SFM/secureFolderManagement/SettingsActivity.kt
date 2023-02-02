package com.SFM.secureFolderManagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.SFM.secureFolderManagement.activities.ComplianceChecker
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

        // to send user to pin code activity
        val changePinPreference: Preference? = findPreference("change_pin")
        changePinPreference?.setOnPreferenceClickListener {
            context?.let { it1 -> getDefaultSharedPreferences(it1).edit().putBoolean("isPinSet", false).apply() }
            val intent = Intent(context, PasswordActivity::class.java)
            startActivity(intent)
            true
        }

        // to send user to compliance checker activity
        val compliancePreference: Preference? = findPreference("check_compliance")
        compliancePreference?.setOnPreferenceClickListener {
            val intent = Intent(context, ComplianceChecker::class.java)
            startActivity(intent)
            true
        }

        // To send user to the web page to change their account password
        val changePasswordPreference : Preference? = findPreference("change_password")
        changePasswordPreference?.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://securefolderwebserver.onrender.com/api/users/changePassword"))
            startActivity(browserIntent)
            true
        }



    }
}