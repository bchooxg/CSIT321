package com.example.secureFolderManagement

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.provider.Settings.Global
import androidx.appcompat.app.AppCompatActivity
import com.example.secureFolderManagement.database.AppDatabase
import com.example.secureFolderManagement.entities.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoggingManager(context: Context) {

    val context = context;
    private lateinit var appDb : AppDatabase


    fun insertLog(action : String, fileName : String? = null) {
        val sp = context.getSharedPreferences(context.resources.getString(R.string.shared_prefs), MODE_PRIVATE)
        val username = sp.getString("username", "")

        if (username == null) {
            android.util.Log.d("loginActivity", "username is null")
            return
        }
        val isoDateTime = java.time.LocalDateTime.now().toString()
        val log = com.example.secureFolderManagement.entities.Log(
            id = null,
            username = username,
            action = action,
            timestamp = isoDateTime,
            fileName = null,
            status = "New"
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDb = AppDatabase.getInstance(context)
            appDb.logDAO().insertLog(log)
        }
    }
}