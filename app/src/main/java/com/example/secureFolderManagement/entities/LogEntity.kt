package com.example.secureFolderManagement.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class LogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "username")
    val username: String?,

    @ColumnInfo(name = "action")
    val action: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: String?,

    @ColumnInfo(name = "fileName")
    val fileName: String?,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "remarks")
    val remarks: String?
)
