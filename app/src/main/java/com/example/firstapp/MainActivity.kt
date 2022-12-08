package com.example.firstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }
}