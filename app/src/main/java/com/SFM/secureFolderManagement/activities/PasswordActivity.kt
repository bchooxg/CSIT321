package com.SFM.secureFolderManagement.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.SFM.secureFolderManagement.LoggingManager
import com.SFM.secureFolderManagement.PreferenceManager
import com.SFM.secureFolderManagement.R
import com.SFM.secureFolderManagement.database.AppDatabase
import com.SFM.secureFolderManagement.interfaces.ApiInterface
import com.SFM.secureFolderManagement.models.BasicResponse
import com.SFM.secureFolderManagement.models.UserLockRequest
import com.SFM.secureFolderManagement.models.UserPollResponse
import com.SFM.secureFolderManagement.models.UserResponse
import com.SFM.secureFolderManagement.services.ServiceBuilder
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PasswordActivity : AppCompatActivity() {

    private val LoggingManager = LoggingManager(this)


    fun toFileManagerActivity(){
        val intent = Intent(this@PasswordActivity, com.SFM.secureFolderManagement.fileList::class.java)
        var path = Environment.getExternalStorageDirectory().path
        // Appending folder name to path
        path += "/"+resources.getString(R.string.folderName)
        intent.putExtra("path", path)
        return startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_screen)

        /* Function Definition Start */
        fun pollUser2(username: String) {
            val errMsg = findViewById<TextView>(R.id.tv_pinScreen_error)
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val response = ServiceBuilder.buildService(ApiInterface::class.java)
            response.pollUser(username).execute().body()?.let {
                Log.v("TEST", "Response: $it")
                if (it.is_locked) {
                    errMsg.text = "User is locked"
                    circleField.setText("")
                } else {
                    toFileManagerActivity()
                }
            }


        }


        // Set Up Password Flow for First Time flow
        fun setUpPassword() {
            val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
            var firstPass = ""
            val tv = findViewById<TextView>(R.id.tv_pinScreen_header)
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            tv.text = "Please set your password"
            val pinLength = sp.getInt("minPass", 4)

            // Set pin length
            circleField.numberOfFields = pinLength
            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {

                    // check if first password is set
                    if (firstPass == "") {
                        firstPass = enteredText
                        circleField.setText("")
                        // edit textview to ask for password again
                        val textView = findViewById<TextView>(R.id.tv_pinScreen_header)
                        textView.text = "Please enter your password again"
                        return false
                    }

                    // check if first password and entered password match
                    else if (firstPass != enteredText) {
                        circleField.setText("")
                        // edit textview to ask for password again
                        val textView = findViewById<TextView>(R.id.tv_pinScreen_header)
                        textView.text = "Passwords do not match. Please enter your password again"
                        return false
                    }
                    // If the passwords match, save the password to shared preferences and move on to next activity
                    else if (firstPass == enteredText){
                        // save password to shared preferences
                        val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("PIN", enteredText)
                            putBoolean("isPinSet", true)
                            commit()
                        }
                        LoggingManager.insertLog("Pin Set")
                        Log.v("TEST", enteredText)
                        toFileManagerActivity();
                        return true
                    }
                    return true
                }
            }

        }
        fun pollUser(username: String) {
            val errMsg = findViewById<TextView>(R.id.tv_pinScreen_error)
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val response = ServiceBuilder.buildService(ApiInterface::class.java)
            response.pollUser(username).enqueue(
                object : Callback<UserPollResponse> {
                    override fun onResponse(
                        call: Call<UserPollResponse>,
                        response: Response<UserPollResponse>
                    ) {
                        val responseBody = response.body()
                        Log.v("TEST", "Response: $responseBody")
                        if (response.isSuccessful) {
                            // Get boolean value from response
                            val isLocked = responseBody?.is_locked
                            // If the user is locked, then show a toast message
                            if(isLocked == true){
                                errMsg.text = "Your account is locked. Please contact your administrator"
                                errMsg.visibility = TextView.VISIBLE
                                circleField.isEnabled = false
                            }else{
                                // Set the tries to 0
                                val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putInt("tries", 0)
                                    putBoolean("isLocked", false)
                                    putBoolean("isPinSet", false)
                                    commit()
                                }
                                errMsg.text = "Your account has been unlocked please try again"
                                errMsg.visibility = TextView.VISIBLE
                                circleField.isEnabled = true
                                setUpPassword()
                            }


                        } else {
                            Toast.makeText(applicationContext, "Req Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserPollResponse>, t: Throwable) {
                        Toast.makeText(this@PasswordActivity, t.toString(), Toast.LENGTH_LONG).show()
                    }

                }
            )



        }

        // Confirm Password Flow
        fun confirmPassword(pin: String) {

            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val tv = findViewById<TextView>(R.id.tv_pinScreen_header)
            val errMsg = findViewById<TextView>(R.id.tv_pinScreen_error)
            val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
            var maxTries = sp.getInt("pinMaxTries", 5)
            val isBioAuthEnabled = sp.getBoolean("isBioAuthEnabled", false)
            var tries = sp.getInt("tries", 0)
            var username = sp.getString("username", "")
            var isLocked = sp.getBoolean("isLocked", false)
            val pinLength = sp.getInt("minPass", 4)
            pollUserDetails(username.toString())

            // create biometric prompt
            if( isBioAuthEnabled){
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext,
                                "Authentication error: $errString", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(applicationContext,
                                "Authentication succeeded!", Toast.LENGTH_SHORT)
                                .show()
                            toFileManagerActivity()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, "Authentication failed",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                biometricPrompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login for Secure File Manager")
                        .setSubtitle("Log in using your biometric credential")
                        .setNegativeButtonText("Cancel")
                        .build())
            }





            // Set pin length
            circleField.numberOfFields = pinLength.toInt()


            tv.text = "Please enter your password"

            // Check if the user is locked
            if(isLocked){
                pollUser(username.toString())
            }

            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {
                    // get max password tries from shared preferences

                    // Refresh tries
                    tries = sp.getInt("tries", 0)


                    if(tries >= maxTries - 1){
                        errMsg.visibility = TextView.VISIBLE
                        errMsg.text = "Tries Exceeded. Please ask your administrator to unlock your account."
                        circleField.setText("")
                        circleField.isEnabled = false
//                        pollUser(username.toString())
                        with(sp.edit()) {
                            putBoolean("isLocked", true)
                            commit()
                        }
                        LoggingManager.insertLog("Locked")
                        lockUser(username.toString())
                        return false
                    }
                    else if (enteredText == pin) {
                        Log.v("TEST", enteredText)
                        with(sp.edit()) {
                            putInt("tries", 0)
                            commit()
                        }
                        LoggingManager.insertLog("Pin Unlock")
                        toFileManagerActivity();
                        return true
                    } else {
                        Log.v("TEST", enteredText)
                        circleField.setText("")
                        tries++
                        with(sp.edit()) {
                            putInt("tries", tries)
                            commit()
                        }
                        errMsg.visibility = TextView.VISIBLE
                        errMsg.text = "Incorrect Password $tries/$maxTries tries"
                        LoggingManager.insertLog("Pin Error")
                        return false
                    }
                }
            }
        }



        /* Driver Code Start  */

        // check if password is set
        val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
        val isPinSet = PreferenceManager(sp).checkPINflag()
        val pin = PreferenceManager(sp).getPIN()
        val isLocked = sp.getBoolean("isLocked", false)
        val username = sp.getString("username", "")

        if(isLocked){
            return pollUser(username.toString())
        }

        if(!isPinSet) {
            setUpPassword()
        } else {
            confirmPassword(pin)
        }



        /* Driver Code END */



    }
    // function that sends a request to server to lock the account
    fun lockUser(username: String){
        Log.v("TEST", "Inside Lock User")
        val apiInterface = ServiceBuilder.buildService(ApiInterface::class.java)
        val requestCall = apiInterface.lockUser(UserLockRequest(username, true))
        requestCall.enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null) {
                        Log.v("TEST", res.toString())
                    }
                }else{
                    Log.v("TEST", "Response not successful")
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Log.v("TEST", "Failed to send request to server")
                Log.v("TEST", t.toString())

            }
        })
    }

    // function that sends a request to server to poll the user and update shared preferences
    fun pollUserDetails(username: String){
        Log.v("TEST", "Inside Poll User Details")
        val apiInterface = ServiceBuilder.buildService(ApiInterface::class.java)
        val requestCall = apiInterface.pollUserDetails(username)
        requestCall.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null) {
                        Log.v("TEST", res.toString())
                    }
                }else{
                    Log.v("TEST", "Response not successful")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.v("TEST", "Failed to send request to server")
                Log.v("TEST", t.toString())

            }
        })
    }
    // Function that sends a request to server to find out if the user account is locked

}
