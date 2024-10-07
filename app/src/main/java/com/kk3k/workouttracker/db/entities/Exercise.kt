package com.kk3k.workouttracker.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Types of exercise
enum class ExerciseType {
    STRENGTH, // Strength exercises
    CARDIO,  // Cardio exercises
    OTHER // Other types of exercises
}

// Target muscles in exercises
enum class TargetMuscle {
    SHOULDERS, // Exercises for shoulders
    CHEST, // Exercises for chest
    BACK, // Exercises for back
    ARMS, // Exercises for arms
    FOREARMS, // Exercises for forearms
    ABS, // Exercises for abs
    LEGS, // Exercises for legs
    CALVES // Exercises for calves
}

@Entity(tableName = "exercise") // Table name
data class Exercise(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0, // Object ID,
    @ColumnInfo(name = "name") var name: String, // Exercise name
    @ColumnInfo(name = "type") var type: ExerciseType, // Type of exercise
    @ColumnInfo(name = "target_muscle") var targetMuscle: TargetMuscle, // Target muscle in exercise
    @ColumnInfo(name = "description") var description: String, // Description of exercise
    @ColumnInfo(name = "image") var image: ByteArray? = null // Exercise image
)
