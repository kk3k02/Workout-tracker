package com.kk3k.workouttracker.db

import androidx.room.TypeConverter

// Converters class to convert complex types to supported types for Room database
class Converters {

    // Convert TargetMuscle enum to String for storage in the database
    @TypeConverter
    fun fromTargetMuscle(value: TargetMuscle): String {
        return value.name  // Return the name of the TargetMuscle enum as a string
    }

}
