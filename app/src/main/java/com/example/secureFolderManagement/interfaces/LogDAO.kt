package com.example.secureFolderManagement.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.secureFolderManagement.entities.LogEntity

// Used to interact with the database for logging purposes
@Dao
interface LogDAO {

    @Query("SELECT * FROM log")
    suspend fun getAllLogs(): List<LogEntity>

    @Query("SELECT * FROM log WHERE username = :username")
    suspend fun getLogsByUsername(username: String): List<LogEntity>

    @Query("SELECT * FROM log WHERE fileName = :fileName")
    suspend fun getLogsByFileName(fileName: String): List<LogEntity>

    @Query("SELECT * FROM log WHERE status = :status")
    suspend fun getLogsByStatus(status: String): List<LogEntity>

    @Insert
    suspend fun insertLog(log: LogEntity)

    @Delete
    suspend fun deleteLog(log: LogEntity)

    @Query("DELETE FROM log")
    suspend fun deleteAllLogs()

    @Query("UPDATE log set status = :newStatus WHERE status = 'New'")
    fun updateLogsStatus(newStatus: String)


}