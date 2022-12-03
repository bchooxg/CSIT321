package com.example.firstapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class FileManager : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        // find button
        val btn = findViewById<Button>(R.id.btnCheckPermissions);
        // set onclick listener and request for permissions if check  permissions function returns false
        btn.setOnClickListener {

            if (Build.VERSION.SDK_INT >= 30) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivity(intent)

                }
            }

            if (!this.checkPermissions()) {
                this.requestPermissions()
            }else{
                // if permissions are already granted, make toast
                Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show()
                // Create intent to go to file list
                val intent = Intent(this, fileList::class.java)
                var path = Environment.getExternalStorageDirectory().path
                path += "/Secure Folder"
                // Check if folder exists
                val folder = File(path)
                if (!folder.exists()) {
                    try{
                        Log.v("Debug", "Folder not found, attempting to create folder")
                        folder.mkdir()
                    }catch (e: Exception){
                        Toast.makeText(this, "Error creating folder", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.v("Debug", "Folder found, moving to folder")
                intent.putExtra("path", path)
                startActivity(intent)
            }
        }

    }

    // Check permission to read and write to external storage
    private fun checkPermissions(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    // Request permission to read and write to external storage
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
     }
}