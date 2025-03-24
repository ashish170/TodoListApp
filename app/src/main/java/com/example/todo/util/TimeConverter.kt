package com.example.todo.util

import androidx.room.TypeConverter
import com.example.todo.core.data.Priority
import java.util.Date

class TimeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromPriority(value: String?): Priority? {
        return value?.let { Priority.valueOf(it) }
    }

    @TypeConverter
    fun priorityToString(priority: Priority?): String? {
        return priority?.name
    }
}
