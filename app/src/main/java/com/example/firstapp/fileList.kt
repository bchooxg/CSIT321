package com.example.firstapp

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.RecoverableSecurityException
import android.content.*
import android.database.Cursor
import android.os.Environment
import android.provider.CalendarContract.CalendarCache.URI
import android.provider.DocumentsContract
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
    private val fileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Create intent to go to file list
                Log.v("TEST", "File list activity started")
            }
        }
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>


    @RequiresApi(Build.VERSION_CODES.R)
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

//                // Create intent to go to file list
//                Log.v("TEST", "Gallery activity started")
//                // get uri from intent
//                val uri = result.data?.data
//                Log.v("TEST", "URI: $uri")
//
//                // get bitmap from uri
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//                // save bitmap as image in secure folder and delete from gallery
//                saveImage(bitmap)
//
//                // get file name from uri
//                val URIPathHelper = URIPathHelper()
//                val fileName = URIPathHelper.getPath(this, uri!!)
//
//                val file = File(fileName)
//
//                // delete file
//                if (file.delete()) {
//                    Log.v("TEST", "File deleted")
//                } else {
//                    Log.v("TEST", "File not deleted")
//                }
//
//                // refresh file list
//                refreshRecyclerView()




                // Create prompt to tell user about file photo deletion
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Move Photo to Secure Folder")
                builder.setMessage("Moving to secure folder will delete photo from gallery and cannot be undone. Continue?")
                builder.setPositiveButton("Yes") { dialog, which ->


                    // Create intent to go to file list
                    Log.v("TEST", "Gallery activity started")
                    // get uri from intent
                    val uri = result.data?.data
                    Log.v("TEST", "URI: $uri")
                    // get bitmap from uri
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    // save bitmap as image in secure folder and delete from gallery
                    saveImage(bitmap)
                    // delete image from gallery
                    val URIPathHelper = URIPathHelper()
                    val fileName = URIPathHelper.getPath(this, uri!!)

                    val photoToBeDeleted = File(fileName)

                    // delete file
                    if (photoToBeDeleted.delete()) {
                        Log.v("TEST", "File deleted")
                    } else {
                        Log.v("TEST", "File not deleted")
                    }

                    // Notify media scanner of deleted file
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(photoToBeDeleted)
                    mediaScanIntent.data = contentUri
                    this.sendBroadcast(mediaScanIntent)

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
    fun saveImage(bitmap: Bitmap?) {
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

        // Create launcher
        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Photo deleted successfully", Toast.LENGTH_SHORT).show()
                    // broadcast to refresh media store
                    this.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(File("/storage/emulated/0/Android/media/com.example.firstapp/secure"))
                        )
                    )

                } else {
                    Toast.makeText(this, "Photo couldn't be deleted", Toast.LENGTH_SHORT).show()
                }
            }

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
    fun openGalleryIntent() {
        // Create intent to go to add photo activity
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        galleryLauncher.launch(intent)

    }

    fun openDirectory(path: String) {
        val intent = Intent(this, fileList::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    fun openFile(uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        var type = "image/*"
        intent.setDataAndType(uri, type)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Make toast
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePhotoToExternalStorage(
        displayName: String,
        bitmap: Bitmap,
        path: String
    ): Boolean {
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

    fun showCreateFolderDialog() {

        // Function prompts user for a folder name and creates a new folder with that name

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_folder_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.etFolderName)

        with(builder) {
            setTitle("Create New Folder")
            setPositiveButton("Create") { dialog, which ->
                val folderName = editText.text.toString()
                val path = intent.getStringExtra("path").toString()
                val file = File(path, folderName)
                if (file.mkdir()) {
                    Toast.makeText(this@fileList, "Folder created", Toast.LENGTH_SHORT).show()
                    // refresh recyclerview
//                    val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
//                    val files = File(path).listFiles()?.toCollection(ArrayList())
//                    recyclerView.adapter = FileListAdapter(files, this@fileList)
                    refreshRecyclerView()

                } else {
                    Toast.makeText(this@fileList, "Folder not created", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }

    fun refreshRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val path = intent.getStringExtra("path").toString()
        val files = File(path).listFiles()?.toCollection(ArrayList())

        // check if there are files in the directory
        val textView = findViewById<TextView>(R.id.tvNoText);
        if (files?.size == 0) {
            textView.visibility = TextView.VISIBLE
        } else {
            textView.visibility = TextView.INVISIBLE
        }
        recyclerView.adapter = FileListAdapter(files, this)
    }

    private fun deleteFileFromURI2(uri: Uri) {

        // get absolute path from uri
        val path = uri.path.toString()


        // Query mediastore for file path and delete file
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(projection[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        val file = File(filePath)
        if (file.delete()) {
            Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "File not deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFileFromURI(uri: Uri) {

        try {
            // android 28 and below
            val rowsDeleted = contentResolver.delete(uri, null, null)
            Log.v("TEST", "Rows deleted: $rowsDeleted")

        } catch (e: SecurityException) {
            // android 29 (Andriod 10)

            // turn provider uri into file uri
            val newUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentUris.parseId(uri)
            )

            Log.v("TEST", "New uri: $newUri")

            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Log.v("TEST", "Android R")
                    // create list of uri
                    val uris = listOf(newUri)
                    Log.v("TEST", "Uris: $uris")
                    MediaStore.createDeleteRequest(contentResolver, uris).intentSender
                }
                // android 30 (Andriod 11)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    Log.v("TEST", "Android Q")
//                    MediaStore.createDeleteRequest(contentResolver, listOf(newUri)).intentSender
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


    private fun deletePhotoFromExternalStorage(photoUri: Uri) {
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

    fun checkEmpty() {
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


class URIPathHelper {

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let {
                context.contentResolver.query(
                    it,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}

