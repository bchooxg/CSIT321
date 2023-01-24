package com.SFM.secureFolderManagement.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.SFM.secureFolderManagement.entities.LogEntity

// Used to interact with the database for logging purposes
@Dao
interface LogDAO {

    @Query("SELECT * FROM log")
    fun getAllLogs(): List<LogEntity>

    @Query("SELECT * FROM log WHERE username = :username")
     fun getLogsByUsername(username: String): List<LogEntity>

    @Query("SELECT * FROM log WHERE fileName = :fileName")
     fun getLogsByFileName(fileName: String): List<LogEntity>

    @Query("SELECT * FROM log WHERE status = :status")
     fun getLogsByStatus(status: String): List<LogEntity>

    @Insert
     fun insertLog(log: LogEntity)

    @Delete
     fun deleteLog(log: LogEntity)

    @Query("DELETE FROM log")
     fun deleteAllLogs()

    @Query("UPDATE log set status = :newStatus WHERE status = 'New'")
    fun updateLogsStatus(newStatus: String)


}