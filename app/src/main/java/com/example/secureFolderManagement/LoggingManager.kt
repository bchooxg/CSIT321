package com.example.secureFolderManagement

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.example.secureFolderManagement.database.AppDatabase
import com.example.secureFolderManagement.interfaces.ApiInterface
import com.example.secureFolderManagement.models.BasicResponse
import com.example.secureFolderManagement.services.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val log = com.example.secureFolderManagement.entities.LogEntity(
            id = null,
            username = username,
            action = action,
            timestamp = isoDateTime,
            fileName = fileName,
            status = "New"
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDb = AppDatabase.getInstance(context)
            appDb.logDAO().insertLog(log)
        }
    }

    fun sendLogs(){
        // get a list of logs with status "New"
        Log.v("TEST", "sendLogs() called")
        GlobalScope.launch {
            appDb = AppDatabase.getInstance(context)
            val logs = appDb.logDAO().getLogsByStatus("New")
            // send logs to server
            val retrofit = ServiceBuilder.buildService(ApiInterface::class.java)
            val logRequest = com.example.secureFolderManagement.models.LogRequest(logs)

            val retrofitData = retrofit.sendLogs(logRequest)
            retrofitData.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    Log.v("TEST", "response: ${response.body()}")
                    if (response.isSuccessful) {
                        Log.v("loginActivity", "Logs sent successfully")
                        // update logs status to "Sent"
                        GlobalScope.launch(Dispatchers.IO) {
                            appDb = AppDatabase.getInstance(context)
                            appDb.logDAO().updateLogsStatus("Sent")
                        }
                    } else {
                        Log.v("TEST", "Logs not sent")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.v("TEST", "Logs not sent")
                }
            })

        }


    }


}