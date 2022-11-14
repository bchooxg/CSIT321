package com.example.firstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class fileList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        // get recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val textView = findViewById<TextView>(R.id.tvNoText);

        // get path from intent
        val path = intent.getStringExtra("path").toString()
        // Get list of files from path
        val files = File(path).listFiles()
        // if there are no files, make textview visible
        if (files != null) {
            if (files.isEmpty()) {
                textView.visibility = TextView.VISIBLE
                return
            }
        }
        
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = FileListAdapter(files, this)
        
    }
}


