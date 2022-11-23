package com.example.firstapp

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.security.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class fileList : AppCompatActivity() {

    // launcher
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Create intent to go to file list
            Log.v("TEST", "File list activity started")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        // get recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val textView = findViewById<TextView>(R.id.tvNoText);
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton);

        // get path from intent
        val path = intent.getStringExtra("path").toString()
        // Get list of files from path
        var files = File(path).listFiles()?.toCollection(ArrayList())

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            // Do something with the bitmap
            savePhotoToExternalStorage(UUID.randomUUID().toString(), it, path)
            Log.v("TEST", "Photo taken")
        }

        // add onclick listener to button
        button.setOnClickListener {
            // show popup menu
            // make toast
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            val popup = PopupMenu(this, button)
            popup.menu.add("Create New Folder")
            popup.menu.add("Add Photo")
            popup.show()

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    "Create New Folder" -> {
                        // create dialog with edit text
                        showCreateFolderDialog()
                        true
                    }
                    "Add Photo" -> {
                        // Create intent to go to add photo activity
//                        openCameraIntent()
                        takePhoto.launch(null)
                        true
                    }
                    else -> false
                }
            }
        }


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
    fun openDirectory(path: String){
        val intent = Intent(this, fileList::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    fun openFile(uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        var type = "image/*"
        intent.setDataAndType(uri, type)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try{
            startActivity(intent)
        }catch (e: Exception){
            // Make toast
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePhotoToExternalStorage(displayName: String, bitmap: Bitmap, path: String): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            //put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        Log.v("TEST", current)
        
        try {
            val file = File(path, "$current.jpg")
            file.createNewFile()
            val fOutStream = FileOutputStream(file)
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                85,
                fOutStream
            ); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOutStream.flush();
            fOutStream.close();
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving photo", Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show()
        // Update recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val files = File(path).listFiles()?.toCollection(ArrayList())
        recyclerView.adapter = FileListAdapter(files, this)

        return true
    }

    fun showCreateFolderDialog(){

        // Function prompts user for a folder name and creates a new folder with that name

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_folder_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.etFolderName)

        with(builder){
            setTitle("Create New Folder")
            setPositiveButton("Create"){ dialog, which ->
                val folderName = editText.text.toString()
                val path = intent.getStringExtra("path").toString()
                val file = File(path, folderName)
                if(file.mkdir()){
                    Toast.makeText(this@fileList, "Folder created", Toast.LENGTH_SHORT).show()
                    // refresh recyclerview
                    val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
                    val files = File(path).listFiles()?.toCollection(ArrayList())
                    recyclerView.adapter = FileListAdapter(files, this@fileList)

                }else{
                    Toast.makeText(this@fileList, "Folder not created", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }





}


