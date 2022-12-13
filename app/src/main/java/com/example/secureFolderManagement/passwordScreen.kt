package com.example.secureFolderManagement

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField


class passwordScreen : AppCompatActivity() {

    fun toFileManagerActivity(){
        val intent = Intent(this@passwordScreen, fileList::class.java)
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
                        val textView = findViewById<TextView>(R.id.tv_pinScreen)
                        textView.text = "Please enter your password again"
                        return false
                    }

                    // check if first password and entered password match
                    if (firstPass != enteredText) {
                        circleField.setText("")
                        // edit textview to ask for password again
                        val textView = findViewById<TextView>(R.id.tv_pinScreen)
                        textView.text = "Passwords do not match. Please enter your password again"
                        return false
                    }

                    // save password to shared preferences
                    val sharedPref = getSharedPreferences(resources.getString(R.string.shared_prefs), MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("PIN", enteredText)
                        putBoolean("isPinSet", true)
                        commit()
                    }


                    Toast.makeText(this@passwordScreen, "Saved Password", Toast.LENGTH_SHORT).show()
                    Log.v("password", enteredText)
                    toFileManagerActivity();

                    return true
                }
            }

        }

        // Confirm Password Flow
        fun confirmPassword(pin: String) {
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            val tv = findViewById<TextView>(R.id.tv_pinScreen)
            tv.text = "Please enter your password"
            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {
                    // save password to shared preferences
                    if (enteredText == pin) {
                        Toast.makeText(this@passwordScreen, "Password Correct", Toast.LENGTH_SHORT).show()
                        Log.v("password", enteredText)
                        toFileManagerActivity();
                        return true
                    } else {
                        Toast.makeText(this@passwordScreen, "Password Incorrect", Toast.LENGTH_SHORT).show()
                        Log.v("password", enteredText)
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
        } else {
            confirmPassword(pin)
        }



        /* Driver Code END */



    }
}
