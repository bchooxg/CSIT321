package com.SFM.secureFolderManagement.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.SFM.secureFolderManagement.R

class ComplianceChecker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compliance_checker)

        // get shared preferences
        val sp = getDefaultSharedPreferences(this)
        val bio_required = sp.getBoolean("requireBiometrics", false)
        val isBioAuth = sp.getBoolean("isBioAuthEnabled", false)

        findViewById<TextView>(R.id.tv_bio_required).text = bio_required.toString()
        findViewById<TextView>(R.id.tv_bio_current).text = isBioAuth.toString()

    }
}