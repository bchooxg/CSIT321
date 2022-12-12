package com.example.secureFolderManagement

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField


class passwordScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_screen)

        /* Function Definition Start */

        // check if password in shared preferences is set
        fun checkPassword(): Boolean {
            val sharedPref = getSharedPreferences("password", MODE_PRIVATE)
            val password = sharedPref.getString("password", "-1")
            return password != "-1"
        }

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
                        val textView = findViewById<TextView>(R.id.textView)
                        textView.text = "Please enter your password again"
                        return false
                    }

                    // check if first password and entered password match
                    if (firstPass != enteredText) {
                        circleField.setText("")
                        // edit textview to ask for password again
                        val textView = findViewById<TextView>(R.id.textView)
                        textView.text = "Passwords do not match. Please enter your password again"
                        return false
                    }

                    // save password to shared preferences
                    val sharedPref = getSharedPreferences("password", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("password", enteredText)
                        commit()
                    }

                    Toast.makeText(this@passwordScreen, "Saved Password", Toast.LENGTH_SHORT).show()
                    Log.v("password", enteredText)


                    return true
                }
            }

        }

        // Confirm Password Flow
        fun confirmPassword() {
            val circleField = findViewById<CirclePinField>(R.id.circleField)
            circleField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
                override fun onTextComplete(enteredText: String): Boolean {
                    // save password to shared preferences
                    val sharedPref = getSharedPreferences("password", MODE_PRIVATE)
                    val password = sharedPref.getString("password", "0000")
                    if (enteredText == password) {
                        Toast.makeText(this@passwordScreen, "Password Correct", Toast.LENGTH_SHORT).show()
                        Log.v("password", enteredText)
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

        setUpPassword()



        /* Driver Code END */



    }
}
