package com.example.secureFolderManagement.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secureFolderManagement.PreferenceManager
import com.example.secureFolderManagement.R
import com.example.secureFolderManagement.interfaces.ApiInterface
import com.example.secureFolderManagement.models.BasicResponse
import com.example.secureFolderManagement.models.UserLockRequest
import com.example.secureFolderManagement.models.UserPollResponse
import com.example.secureFolderManagement.services.ServiceBuilder
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PasswordActivity : AppCompatActivity() {

    fun toFileManagerActivity(){
        val intent = Intent(this@PasswordActivity, com.example.secureFolderManagement.fileList::class.java)
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


        // Set Up Password Flow for First Time flow
        fun setUpPassword() {
            val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
            var firstPass = ""
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val pinLength = sp.getInt("minPass", 4)

            // Set pin length
            circleField.numberOfFields = pinLength.toInt()
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
                    if (firstPass != enteredText) {
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
                        Toast.makeText(this@PasswordActivity, "Saved Password", Toast.LENGTH_SHORT).show()
                        Log.v("TEST", enteredText)
                        toFileManagerActivity();
                        return true
                    }
                    return true
                }
            }

        }

        // Confirm Password Flow
        fun confirmPassword(pin: String) {
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val tv = findViewById<TextView>(R.id.tv_pinScreen_header)
            val errMsg = findViewById<TextView>(R.id.tv_pinScreen_error)
            val sp = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
            var maxTries = sp.getInt("maxTries", 5)
            var tries = sp.getInt("tries", 0)
            var username = sp.getString("username", "")
            var isLocked = sp.getBoolean("isLocked", false)
            val pinLength = sp.getInt("minPass", 4)

            // Set pin length
            circleField.numberOfFields = pinLength.toInt()

            fun pollUser(username: String) {
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
                                        commit()
                                    }
                                    errMsg.text = "Your account has been unlocked please try again"
                                    errMsg.visibility = TextView.VISIBLE
                                    circleField.isEnabled = true
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

            tv.text = "Please enter your password"

            // Check if the user is locked
            if(isLocked){
                pollUser(username.toString())
            }

            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {
                    // get max password tries from shared preferences


                    if(tries >= maxTries){
                        errMsg.visibility = TextView.VISIBLE
                        errMsg.text = "Tries Exceeded. Please ask your administrator to unlock your account."
                        circleField.setText("")
                        circleField.isEnabled = false
//                        pollUser(username.toString())
                        with(sp.edit()) {
                            putBoolean("isLocked", true)
                            commit()
                        }
                        lockUser(username.toString())
                        return false
                    }
                    else if (enteredText == pin) {
                        Log.v("TEST", enteredText)
                        with(sp.edit()) {
                            putInt("tries", 0)
                            commit()
                        }
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


        if(!isPinSet) {
            setUpPassword()
            PreferenceManager(sp).setPINflag(true)
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

    // Function that sends a request to server to find out if the user account is locked

}
