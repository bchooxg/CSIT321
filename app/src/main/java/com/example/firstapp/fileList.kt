package com.example.firstapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.os.Environment
import android.provider.CalendarContract.CalendarCache.URI
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toFile
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class fileList : AppCompatActivity() {

    // launcher
    private val fileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Create intent to go to file list
            Log.v("TEST", "File list activity started")
        }
    }
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    @RequiresApi(Build.VERSION_CODES.R)
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            // Create prompt to tell user about file photo deletion
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Move Photo to Secure Folder")
            builder.setMessage("Moving to secure folder will delete photo from gallery and cannot be undone. Continue?")
            builder.setPositiveButton("Yes") { dialog, which ->


                // Create intent to go to file list
                Log.v("TEST", "Gallery activity started")
                // get uri from intent
                val uri  = result.data?.data
                Log.v("TEST", "URI: $uri")
                // get bitmap from uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                // save bitmap to file
                saveImage(bitmap)

                // get file path from uri
                val parentDir = intent.getStringExtra("path").toString()
                val filePath = uri?.path

                // Get media number from uri
                val mediaNumber = filePath?.substringAfterLast("/").toString()
//                val newURI = Uri.parse("content://media/external/images/media/$mediaNumber")

                val newURI = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaNumber.toLong())
                Log.v("TEST", "Media Number: $mediaNumber")
                Log.v("TEST", "New URI: $newURI")
                // Create delete request to delete photo from gallery

                // Query for the file to be deleted.
                val queryUri = MediaStore.Files.getContentUri("external")
                val projection = arrayOf(MediaStore.Files.FileColumns._ID)
                val selection = "${MediaStore.Files.FileColumns._ID} = ?"
                val selectionArgs = arrayOf(mediaNumber)
                val cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)
                cursor?.use {
                    // Confirm that it exists.
                    if (it.moveToFirst()) {
                        // We found the ID. Deleting the item via the content provider will also remove the file.
                        Log.v("TEST", "File found, Attempting to delete")
                        val deleteUri = ContentUris.withAppendedId(queryUri, mediaNumber.toLong())
                        val rows = contentResolver.delete(deleteUri, null, null)
                        Log.v("TEST", "Rows deleted: $rows")
                    } else {
                        // File not found in media store DB
                        Log.v("TEST", "File not found in media store DB")
                    }
                }




                // refresh file list
                refreshRecyclerView()

            }
            builder.setNegativeButton("No") { dialog, which ->
                // close dialog
                dialog.dismiss()
            }
            builder.show()
        }

    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveImage(bitmap: Bitmap?) {
        // get current date and time
        val current = LocalDateTime.now()
        // format date and time
        val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
        val formatted = current.format(formatter)
        // create file name
        val fileName = "IMG_$formatted.jpg"
        // Get filepath from intent
        val path = intent.getStringExtra("path")
        // create file
        val file = File(path, fileName)
        // create file output stream
        val fOut = FileOutputStream(file)
        // compress bitmap to file
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        // flush file output stream
        fOut.flush()
        // close file output stream
        fOut.close()
        // add file to media store
        //MediaStore.Images.Media.insertImage(this.contentResolver, file.absolutePath, file.name, file.name)
        // refresh media store
        //this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        // make toast
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

//        // Create launcher
//        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
//            if(it.resultCode == RESULT_OK) {
//                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
//                    delete
//                }
//                Toast.makeText(this, "Photo deleted successfully", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Photo couldn't be deleted", Toast.LENGTH_SHORT).show()
//            }
//        }

        // get recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton);

        // get path from intent
        val path = intent.getStringExtra("path").toString()
        // Get list of files from path
        var files = File(path).listFiles()?.toCollection(ArrayList())

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            // Do something with the bitmap
            savePhotoToExternalStorage(UUID.randomUUID().toString(), it, path)
        }

        // add onclick listener to button
        button.setOnClickListener {
            // show popup menu
            // make toast
            val popup = PopupMenu(this, button)
            popup.menu.add("Create New Folder")
            popup.menu.add("Add Photo")
            popup.menu.add("Select from gallery")
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
                    "Select from gallery" -> {
                        // Create intent to go to add photo activity
                        openGalleryIntent()
                        true
                    }
                    else -> false
                }
            }
        }

        //checkEmpty()
        
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        refreshRecyclerView()
//        recyclerView.adapter = FileListAdapter(files, this)
        // check if there are files in the directory

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun openGalleryIntent() {
        // Create intent to go to add photo activity
        val intent = Intent(Intent.ACTION_PICK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.type = "image/*"
        galleryLauncher.launch(intent)

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

        val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
        val current = LocalDateTime.now().format(formatter)
        Log.v("TEST", current)
        
        try {
            val file = File(path, "IMG_$current.jpg")
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
//        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
//        val files = File(path).listFiles()?.toCollection(ArrayList())
//        recyclerView.adapter = FileListAdapter(files, this)
        refreshRecyclerView()

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
//                    val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
//                    val files = File(path).listFiles()?.toCollection(ArrayList())
//                    recyclerView.adapter = FileListAdapter(files, this@fileList)
                    refreshRecyclerView()

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

    fun refreshRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val path = intent.getStringExtra("path").toString()
        val files = File(path).listFiles()?.toCollection(ArrayList())

        // check if there are files in the directory
        val textView = findViewById<TextView>(R.id.tvNoText);
        if(files?.size == 0) {
            textView.visibility = TextView.VISIBLE
        }else{
            textView.visibility = TextView.INVISIBLE
        }
        recyclerView.adapter = FileListAdapter(files, this)
    }

    fun deleteFileFromURI(uri: Uri): Int {

        try{
            // android 28 and below
            val rowsDeleted = contentResolver.delete(uri, null, null)
            Log.v("TEST", "Rows deleted: $rowsDeleted")
            return rowsDeleted
        }catch (e : SecurityException){
            // android 29 (Andriod 10)
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
                }
                // android 30 (Andriod 11)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            intentSender?.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }
        return 0
    }
    private fun deletePhotoFromExternalStorage(photoUri: Uri){
        try {
            contentResolver.delete(photoUri, null, null)
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(contentResolver, listOf(photoUri)).intentSender
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            intentSender?.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }
    }

    fun checkEmpty(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val textView = findViewById<TextView>(R.id.tvNoText);
        val path = intent.getStringExtra("path").toString()
        val files = File(path).listFiles()?.toCollection(ArrayList())
        if (files != null) {
            if (files.isEmpty()) {
                textView.visibility = TextView.VISIBLE
                return
            }
        }
        textView.visibility = TextView.INVISIBLE
    }





}


