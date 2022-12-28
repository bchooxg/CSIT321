package com.example.secureFolderManagement.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.secureFolderManagement.entities.Log
import retrofit2.http.DELETE

// Used to interact with the database for logging purposes
@Dao
interface LogDAO {

    @Query("SELECT * FROM log")
    suspend fun getAllLogs(): List<Log>

    @Query("SELECT * FROM log WHERE username = :username")
    suspend fun getLogsByUsername(username: String): List<Log>

    @Query("SELECT * FROM log WHERE fileName = :fileName")
    suspend fun getLogsByFileName(fileName: String): List<Log>

    @Insert
    suspend fun insertLog(log: Log)

    @Delete
    suspend fun deleteLog(log: Log)

    @Query("DELETE FROM log")
    suspend fun deleteAllLogs()



}