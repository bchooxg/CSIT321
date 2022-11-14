package com.example.firstapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class FileManager : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        // find button
        val btn = findViewById<Button>(R.id.btnCheckPermissions);
        // set onclick listener and request for permissions if checkpermissions function returns false
        btn.setOnClickListener {
            if (!this.checkPermissions()) {
                this.requestPermissions()
            }else{
                // if permissions are already granted, make toast
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
                // Create intent to go to file list
                val intent = Intent(this, fileList::class.java)
                var path = Environment.getExternalStorageDirectory().path
                intent.putExtra("path", path)
                startActivity(intent)
            }
        }

    }

    // Check permission to read and write to external storage
    fun checkPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    // Request permission to read and write to external storage
     fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
     }
}