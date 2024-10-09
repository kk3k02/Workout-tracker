package com.kk3k.workouttracker.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTargetMuscle(value: TargetMuscle): String {
        return value.name
    }

    @TypeConverter
    fun toTargetMuscle(value: String): TargetMuscle {
        return TargetMuscle.valueOf(value)
    }
}
