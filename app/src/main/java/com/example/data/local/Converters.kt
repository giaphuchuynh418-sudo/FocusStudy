package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Priority
import com.example.data.model.SessionType

class Converters {
    @TypeConverter
    fun fromSessionType(type: SessionType): String = type.name

    @TypeConverter
    fun toSessionType(value: String): SessionType = try {
        SessionType.valueOf(value)
    } catch (_: Exception) {
        SessionType.POMODORO
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = try {
        Priority.valueOf(value)
    } catch (_: Exception) {
        Priority.MEDIUM
    }
}
