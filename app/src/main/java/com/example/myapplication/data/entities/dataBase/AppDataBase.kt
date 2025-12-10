package com.example.myapplication.data.entities.dataBase

import PasswordEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.PasswordDao

@Database(entities = [PasswordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
}
