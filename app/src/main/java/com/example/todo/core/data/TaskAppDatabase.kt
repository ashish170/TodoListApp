package com.example.todo.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todo.util.TimeConverter

@Database(entities = [TaskEntity::class], version = 1,exportSchema = false)
@TypeConverters(TimeConverter::class)
abstract class TaskAppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}