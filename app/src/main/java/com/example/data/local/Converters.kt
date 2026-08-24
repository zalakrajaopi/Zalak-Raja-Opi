package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.HabitType
import com.example.data.model.Mood

class Converters {
    @TypeConverter
    fun fromHabitType(type: HabitType): String = type.name

    @TypeConverter
    fun toHabitType(value: String): HabitType = try {
        HabitType.valueOf(value)
    } catch (e: Exception) {
        HabitType.CUSTOM
    }

    @TypeConverter
    fun fromMood(mood: Mood): String = mood.name

    @TypeConverter
    fun toMood(value: String): Mood = try {
        Mood.valueOf(value)
    } catch (e: Exception) {
        Mood.OKAY
    }
}
