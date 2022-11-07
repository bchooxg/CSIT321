package com.example.firstapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class passwordScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_screen)

//        Set the value of text view on first load
        val tvPassword = findViewById<TextView>(R.id.tvPassChars)
        val btn1 = findViewById<Button>(R.id.materialButton1)
        val btn2 = findViewById<Button>(R.id.materialButton2)
        val btn3 = findViewById<Button>(R.id.materialButton3)
        val btn4 = findViewById<Button>(R.id.materialButton4)
        val btn5 = findViewById<Button>(R.id.materialButton5)
        val btn6 = findViewById<Button>(R.id.materialButton6)
        val btn7 = findViewById<Button>(R.id.materialButton7)
        val btn8 = findViewById<Button>(R.id.materialButton8)
        val btn9 = findViewById<Button>(R.id.materialButton9)
        val btnDelete = findViewById<Button>(R.id.materialButtonDelete)
        val buttons = arrayListOf<Button>(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9)

        fun passcodeChangeHandler(text : String){
            val currPass = tvPassword.text.toString();
            // First iter = "----"
            // Second iter = "●---"
            var newPassChars = ""
            var updateFlag = false // Flag to know if character has already been updated
            for (char in currPass){
                if(char == '-' && !updateFlag){
                    newPassChars += "●"
                    updateFlag = true
                }else{
                    newPassChars += char;
                }
            }
            Log.v("PASSCODE", newPassChars);
            tvPassword.text = newPassChars

        }
        fun passcodeDeleteHandler(){
            val currPass = tvPassword.text.toString();
            var reversed_newPass = ""
            var updateFlag = false
            for (n in currPass.length-1 downTo 0) {
                if( currPass[n].equals('●') && !updateFlag ){
                    reversed_newPass += "-"
                    updateFlag = true
                }else{
                    reversed_newPass += currPass[n]
                }
            }
            tvPassword.text = reversed_newPass.reversed();

        }
        for (button in buttons){
            button.setOnClickListener{
                val btnValue = button.text.toString()
                val toast: Toast = Toast.makeText(this, "${btnValue} was tapped", Toast.LENGTH_LONG)
                passcodeChangeHandler(btnValue)
                toast.show()
            }
        }
        btnDelete.setOnClickListener{
            passcodeDeleteHandler()
        }









    }
}