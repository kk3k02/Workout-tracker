package com.kk3k.workouttracker.data

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
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Object ID,
    val name: String, // Exercise name
    val type: ExerciseType, // Type of exercise
    val targetMuscle: TargetMuscle, // Target muscle in exercise
    val description: String, // Description of exercise
    val image: ByteArray? = null // Exercise image
)
