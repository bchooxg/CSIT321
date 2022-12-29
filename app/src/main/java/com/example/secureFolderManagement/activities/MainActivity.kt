package com.example.secureFolderManagement.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import com.example.secureFolderManagement.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up the view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Set up shared preferences
        val sharedPreferences = getSharedPreferences(resources.getString(R.string.shared_prefs), Context.MODE_PRIVATE)
        val preferenceManager = PreferenceManager(sharedPreferences)
        val username = preferenceManager.checkUsername()
        Log.v("TEST", "Username: $username")
        val auth = preferenceManager.checkAuth()
        val onboarding = preferenceManager.checkOnboarding()
        val isPinSet = preferenceManager.checkPINflag()
        val loggingManager = LoggingManager(this)
        Log.v("TEST", "Auth: $auth, Onboarding: $onboarding, isPinSet: $isPinSet")

        // Direct the user to diff activities based on flags in shared prefs

        if (!auth){
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
        else if (!onboarding) {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }else{
            // Go to pin code activity
            val intent = Intent(this, PasswordActivity::class.java)
            loggingManager.sendLogs()
            startActivity(intent)
        }



//        val intent = Intent(this, fileList::class.java)
//        var path = Environment.getExternalStorageDirectory().path
//        // Appending folder name to path
//        path += "/" + resources.getString(R.string.folderName)
//        intent.putExtra("path", path)
//        Log.v("TEST", "Path: $path")
//        startActivity(intent)

        // Legacy logic for debugging purposes

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }

        val btn2 = findViewById<Button>(R.id.button2)
        btn2.setOnClickListener {
            val intent = Intent(this, FileManager::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }
        
        val btn3 = findViewById<Button>(R.id.button3)
        btn3.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }
        val btn4 = findViewById<Button>(R.id.button4)
        btn4.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }

        val btn5 = findViewById<Button>(R.id.button5)
        btn5.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }
}