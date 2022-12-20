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
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField


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
            var firstPass = ""
            val circleField = findViewById<CirclePinField>(R.id.circleField)
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
                    if (firstPass == enteredText){
                        // save password to shared preferences
                        val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("PIN", enteredText)
                            putBoolean("isPinSet", true)
                            commit()
                        }
                        return true
                    }
                    // check if first password and entered password match
                    else if (firstPass != enteredText) {
                        circleField.setText("")
                        // edit textview to ask for password again
                        val textView = findViewById<TextView>(R.id.tv_pinScreen_header)
                        textView.text = "Passwords do not match. Please enter your password again"
                        return false
                    }




                    Toast.makeText(this@PasswordActivity, "Saved Password", Toast.LENGTH_SHORT).show()
                    Log.v("TEST", enteredText)
                    toFileManagerActivity();

                    return true
                }
            }

        }

        // Confirm Password Flow
        fun confirmPassword(pin: String) {
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val tv = findViewById<TextView>(R.id.tv_pinScreen_header)
            val errMsg = findViewById<TextView>(R.id.tv_pinScreen_error)
            // get max password tries from shared preferences
            val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
            val maxTries = sharedPref.getInt("maxTries", 5)
            var tries = sharedPref.getInt("tries", 0)

            tv.text = "Please enter your password"
            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {
                    // save password to shared preferences

                    if(tries >= maxTries){
                        errMsg.visibility = TextView.VISIBLE
                        errMsg.text = "Tries Exceeded. Please ask your administrator to unlock your account."
                        return false
                    }
                    else if (enteredText == pin) {
                        Log.v("TEST", enteredText)
                        with(sharedPref.edit()) {
                            putInt("tries", 0)
                            commit()
                        }
                        toFileManagerActivity();
                        return true
                    } else {
                        Log.v("TEST", enteredText)
                        circleField.setText("")
                        tries++
                        with(sharedPref.edit()) {
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
        // Subsequent Flow for Password Entry




        /* Function Definition END */

        /* Driver Code Start */

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
}
