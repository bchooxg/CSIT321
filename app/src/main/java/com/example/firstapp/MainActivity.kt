package com.example.firstapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up the view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up shared preferences
        val sharedPreferences = getSharedPreferences("com.example.firstapp", Context.MODE_PRIVATE)
        val preferenceManager = PreferenceManager(sharedPreferences)
        val username = preferenceManager.checkUsername()
        Log.v("MainActivity", "Username: $username")


        // Add logic

        val btn = findViewById<Button>(R.id.button);
        btn.setOnClickListener {
            val intent = Intent(this, passwordScreen::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }

        val btn2 = findViewById<Button>(R.id.button2);
        btn2.setOnClickListener {
            val intent = Intent(this, FileManager::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }
        
        val btn3 = findViewById<Button>(R.id.button3);
        btn3.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            intent.putExtra("key", "Test")
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }
}