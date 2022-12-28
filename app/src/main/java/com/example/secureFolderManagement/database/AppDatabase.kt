package com.example.secureFolderManagement.database

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.secureFolderManagement.entities.Log
import com.example.secureFolderManagement.interfaces.LogDAO

@Database(entities = [Log::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun logDAO(): LogDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "log_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }


}