package com.example.secureFolderManagement

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<MaterialButton>(R.id.loginBtn)

        loginBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
        })
    }
}