package com.SFM.secureFolderManagement

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import java.io.File

class CustomSlidePolicyFragment : Fragment(), SlidePolicy {

    private lateinit var imageView: ImageView
    private lateinit var button : Button
    private lateinit var storagePermissionResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.intro_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set up the views
        super.onViewCreated(view, savedInstanceState)
        // Set up launcher
        storagePermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
                checkPermissions()
        }


        imageView = view.findViewById(R.id.iv_intro_fragment)
        button = view.findViewById(R.id.btn_intro_fragment)
        // Check for permissions on screen load
        checkPermissions()
        button.setOnClickListener {
            // Ask for permissions on button click
            if(!checkPermissions()){
                requestPermissions()
            }
        }
    }

    fun checkPermissions(): Boolean {
        var validFlag = false
        // Separation of logic based on Android version
        if (Build.VERSION.SDK_INT >= 30) {
            Log.v("TEST", "SDK is 30 or greater")
            Log.v("TEST", "isExternalStorageManager: ${Environment.isExternalStorageManager()}")
            validFlag = Environment.isExternalStorageManager()

        }else{
            Log.v("TEST", "SDK is less than 30")
            // Request for read/write permissions
            validFlag = checkReadWritePermissions()
        }

        // Combined logic by checking flag
        if (validFlag){
            // If permissions are granted, set image to green check
            imageView.setImageResource(R.drawable.ic_baseline_check_24_green)
            imageView.tag = R.drawable.ic_baseline_check_24_green
            checkOrCreateSecureFolder()

        } else {
            // If permissions are not granted, set image to red x
            imageView.setImageResource(R.drawable.ic_baseline_do_not_disturb_24)
            imageView.tag = R.drawable.ic_baseline_do_not_disturb_24
        }
        return validFlag
    }

    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >= 30) {
            Log.v("TEST", "Requesting for manage external storage permissions")
            requestManagePermissions()
        }else{
            Log.v("TEST", "Requesting for read write storage permissions")
            requestReadWritePermissions()
        }
    }

    private fun checkReadWritePermissions(): Boolean {
        val readPermission = requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return readPermission == android.content.pm.PackageManager.PERMISSION_GRANTED && writePermission == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadWritePermissions() {
        val result = context?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Log.v("TEST", "Result from checking permissions: $result")
        if (result == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // If permissions are granted, set image to green check
            imageView.setImageResource(R.drawable.ic_baseline_check_24_green)
            imageView.tag = R.drawable.ic_baseline_check_24_green
            checkOrCreateSecureFolder()
        } else {
            // If permissions are not granted, set image to red x
            imageView.setImageResource(R.drawable.ic_baseline_do_not_disturb_24)
            imageView.tag = R.drawable.ic_baseline_do_not_disturb_24
            // Request for read/write permissions
            // check if user has denied permission before
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "Secure folder needs files permission to operate", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Please enable from settings to proceed", Toast.LENGTH_LONG).show()
                // open settings for app permissions
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)

            }
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManagePermissions(){
        Log.v("TEST", "Requesting permissions")
        // create intent to open settings page
        val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data = Uri.parse(activity?.applicationContext?.let { String.format("package:%s", it.packageName) })
        storagePermissionResultLauncher.launch(intent)
    }

    override val isPolicyRespected: Boolean
        get() =  imageView.tag == R.drawable.ic_baseline_check_24_green

    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(
            requireContext(),
            "Please grant the permissions to continue",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun checkOrCreateSecureFolder(){
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/" + resources.getString(R.string.folderName))
        if (!dir.exists()) {
            Log.v("TEST", "Folder not found, attempting to create folder")
            try{
                dir.mkdir()
                Log.v("TEST", "Folder created")
            } catch (e: Exception){
                Log.v("TEST", "Folder creation failed")
            }
        }else {
            Log.v("TEST", "Folder found")
        }
    }


    companion object {
        fun newInstance() : CustomSlidePolicyFragment {
            return CustomSlidePolicyFragment()
        }
    }
}