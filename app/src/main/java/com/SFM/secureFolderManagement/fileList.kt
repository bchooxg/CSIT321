package com.SFM.secureFolderManagement

import android.app.Activity
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC
import com.SFM.secureFolderManagement.activities.loginActivity
import com.SFM.secureFolderManagement.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class fileList : AppCompatActivity() {

    // launcher
    private val loggingManager = LoggingManager(this)
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

                    // Get file path from current intent
//                    saveImage(bitmap)
                    saveEncryptedImage(bitmap)
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

    private val storageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Create intent to go to file list
                Log.v("TEST", "Storage activity started")
                val uri = result.data?.data
                Log.v("TEST", "URI: $uri")

                // Get file path from uri
                val URIPathHelper = URIPathHelper()
                val filePath = URIPathHelper.getPath(this, uri!!)
                Log.v("TEST", "File Path: $filePath")
                filePath?.let { File(it) }?.let { saveEncryptedFile(it) }

                refreshRecyclerView()
            }
        }

    fun saveEncryptedFile(file: File){

        // Create a location to store the encrypted file
        val encryptedFileName = "E_${file.name}"
        val path = intent.getStringExtra("path")
        val encryptedFileLocation = File(path, encryptedFileName)
        // Create master key
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Read from file and write to encrypted file
        val encryptedFile = EncryptedFile.Builder(
            encryptedFileLocation,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val fileInputStream = file.inputStream()
        val fileOutputStream = encryptedFile.openFileOutput()
        fileInputStream.copyTo(fileOutputStream)
        fileInputStream.close()
        fileOutputStream.close()
        loggingManager.insertLog("Saved File", file.absolutePath)


    }

    fun saveEncryptedImage(bitmap: Bitmap)
    {
        val fileName = generateFileName("E_IMG_", ".jpg")
        val path = intent.getStringExtra("path")
        val file = File(path, fileName)
        // turn bitmap into byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        val masterKeyAlias = MasterKeys.getOrCreate(AES256_GCM_SPEC)
        val encryptedFile = EncryptedFile.Builder(
            file,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        encryptedFile.openFileOutput().use {
            it.write(bitmapData)
        }
        loggingManager.insertLog("Saved File", file.absolutePath)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun saveImage(bitmap: Bitmap?) {

        val fileName = generateFileName("IMG_", ".jpg")
        // Get filepath from intent
        val path = intent.getStringExtra("path")
        // create file
        val file = File(path, fileName)
        val fOut = FileOutputStream(file)




        // compress bitmap to file
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        // flush file output stream
        fOut.flush()
        // close file output stream
        fOut.close()

        // todo remove normal file save

        // make toast
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
        refreshRecyclerView()
        loggingManager.insertLog("Saved File", file.absolutePath)

    }

    // Set up menu for toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    // Set up menu item click listener
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ab_option_settings -> {
                // Create intent to go to settings
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.ab_option_logOut
            -> {
                // Create intent to go to about
                val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
                PreferenceManager(sp).logout()
                val intent = Intent(this, loginActivity::class.java)
                loggingManager.insertLog("Logout")
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        // Set up action bar and actions
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)



        // Create launcher
        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Photo deleted successfully", Toast.LENGTH_SHORT).show()
                    // broadcast to refresh media store
                    this.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(File("/storage/emulated/0/Android/media/com.SFM.firstapp/secure"))
                        )
                    )

                } else {
                    Toast.makeText(this, "Photo couldn't be deleted", Toast.LENGTH_SHORT).show()
                }
            }

        // get recyclerview
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        // create temp variable to hold temp uri
        var tempUri: Uri? = null

        // get path from intent
        val path = intent.getStringExtra("path").toString()
        Log.v("TEST", "Current Folder Path: $path")
        // Get list of files from path
        var files = File(path).listFiles()?.toCollection(ArrayList())

        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if(it) {
                // refresh recyclerview
                Log.v("TEST", "Photo taken ActivityResult return true")

                // Log value of tempUri
                Log.v("TEST", "TempUri: $tempUri")
                // get bitmap from uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, tempUri)
                // save bitmap as image in secure folder
//                saveImage(bitmap)
                saveEncryptedImage(bitmap)
                Log.v("TEST", "Photo saved")

                refreshRecyclerView()
            }else{
                Log.v("TEST", "Photo Not Taken ActivityResult return False")

            }

        }

        // add onclick listener to button
        button.setOnClickListener {
            // show popup menu
            // make toast
            val popup = PopupMenu(this, button)
            popup.menu.add("Create New Folder")
            popup.menu.add("Take Photo")
            popup.menu.add("Select from gallery")
            popup.menu.add("Select from device")
            popup.show()

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    "Create New Folder" -> {
                        // create dialog with edit text
                        createDirectory()
                        true
                    }
                    "Take Photo" -> {
                        // Create intent to go to add photo activity
                        tempUri = initTempUri()
                        openCameraIntent(cameraLauncher, tempUri!!)
                        true
                    }
                    "Select from gallery" -> {
                        // Create intent to go to add photo activity
                        openGalleryIntent()
                        true
                    }
                    "Select from device" -> {
                        // Create intent to select file from device
                        openStorageIntent()
                        true
                    }
                    else -> false
                }
            }
        }

        //checkEmpty()

        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = FileListAdapter(files, this)
        refreshRecyclerView()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateFileName(prefix: String, suffix:String):String{
        val current = LocalDateTime.now()
        // format date and time
        val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
        val formatted = current.format(formatter)
        return "$prefix$formatted$suffix"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openCameraIntent(launcher: ActivityResultLauncher<Uri>, tempUri: Uri) {


        launcher.launch(tempUri)

    }

    fun openStorageIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*")
        storageLauncher.launch(intent)
    }

    fun openDirectory(path: String) {

        loggingManager.insertLog("Open Directory" , fileName = path)

        val intent = Intent(this, fileList::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }


    // function to decrypt image
    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptImage(context: Context, target: File): Uri? {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val file = EncryptedFile.Builder(
            context,
            target, mainKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        )
            .build()

        val stream: InputStream = file.openFileInput()
        val bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        // get temp uri for image
        val tempUri = initTempUri()
        // save bitmap to temp uri
        saveBitmapToUri(bitmap, tempUri)
        return tempUri;
    }

    private fun saveBitmapToUri(bitmap: Bitmap?, tempUri: Uri) {
        val outputStream = contentResolver.openOutputStream(tempUri)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
    }

    fun openFile(uri: Uri?) {
        // Check extension of file
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        Log.v("TEST", "Extension: $extension")

        loggingManager.insertLog("Open File", fileName = uri.toString())

        val filename = uri?.path?.substringAfterLast("/")
        val encryptionFlag = filename?.startsWith("E_")
        Log.v("TEST", "Encryption Flag: $encryptionFlag")

        if (extension == "jpg" || extension == "png") {

            val intent = Intent(Intent.ACTION_VIEW)

            if (encryptionFlag == true) {
                // decrypt image
                val decryptedUri = decryptImage(this, File(uri.path.toString()))
                // set uri to decrypted uri
                intent.setDataAndType(decryptedUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                return startActivity(intent)

            }

            intent.setDataAndType(uri, "image/*")
            startActivity(intent)
        }
//        val intent = Intent(Intent.ACTION_VIEW)
//        var type = "image/*"
//        intent.setDataAndType(uri, type)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
//            // Make toast
//            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
//        }
    }





    fun refreshRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvFilesList)
        val path = intent.getStringExtra("path").toString()
        val files = File(path).listFiles()?.toCollection(ArrayList())

        // check if there are files in the directory
        val textView = findViewById<TextView>(R.id.tvNoText)
        if (files?.size == 0) {
            textView.visibility = TextView.VISIBLE
        } else {
            textView.visibility = TextView.INVISIBLE
        }
        recyclerView.adapter = FileListAdapter(files, this)
    }

    // Function to take a file and encrypt it using jetpack security
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptFile(file: File) {
        val keyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val encryptedFileLocation = File(file.parent, "encrypted_${file.name}")
        val encryptedFile =  EncryptedFile.Builder(
            encryptedFileLocation,
            this,
            keyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        // write to encrypted file
        encryptedFile.openFileOutput().apply {
            write(file.readBytes())
            close()
        }

    }



    private fun initTempUri(): Uri {
        //gets the temp_images dir
        val tempImagesDir = File(
            applicationContext.filesDir, //this function gets the external cache dir
            getString(R.string.temp_images_dir)) //gets the directory for the temporary images dir

        tempImagesDir.mkdir() //Create the temp_images dir

        //Creates the temp_image.jpg file
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            getString(R.string.temp_image)) //gets the abstract temp_image file name

        //Returns the Uri object to be used with ActivityResultLauncher
        return FileProvider.getUriForFile(
            applicationContext,
            getString(R.string.authorities),
            tempImage)
    }
    fun createDirectory() {

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
                    loggingManager.insertLog("Create Directory", fileName = file.absolutePath)

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

    fun delete(file: File) {
        Log.v("TEST", file.canWrite().toString())
        Log.v("TEST", file.canRead().toString())
        Log.v("TEST", file.canExecute().toString())

        val fileObj = File(file.absolutePath)
        val isDir = fileObj.isDirectory
        var deleted = false
        if(isDir){
            // If file is directory, delete directory
            Log.v("TEST", "Deleting directory")
            deleted = fileObj.deleteRecursively()
            Log.v("TEST", "Deleted: $deleted")
        }else {
            deleted = fileObj.delete()

        }
        Log.v("TEST", "Deleted: $deleted")
        if(!deleted){
            Toast.makeText(this, "Item not deleted", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
        loggingManager.insertLog("Deleted", file.absolutePath)

        // Update recycler view
        refreshRecyclerView()
    }

    fun rename(file: File) {

            // Function prompts user for a folder name and creates a new folder with that name

            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.create_folder_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.etFolderName)

            with(builder) {
                setTitle("Rename File")
                setPositiveButton("Rename") { dialog, which ->
                    val fileName = editText.text.toString()
                    val path = intent.getStringExtra("path").toString()

                    // rename file
                    val newFile = File(path, fileName)
                    if (file.renameTo(newFile)){
                        Log.v("TEST", "File renamed")
                        loggingManager.insertLog("Renamed", file.absolutePath, newFile.absolutePath)
                    }else{
                        Log.v("TEST", "File not renamed")
                    }

                    refreshRecyclerView()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
                show()
            }

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

