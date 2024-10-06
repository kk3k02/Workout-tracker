package com.kk3k.workouttracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "set", // Table name
    foreignKeys = [
        ForeignKey( // Foreign key in Workout
            entity = Workout::class,
            parentColumns = ["uid"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey( // Foreign key in Exercise
            entity = Exercise::class,
            parentColumns = ["uid"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Set(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Object ID
    val workoutId: Int, // Workout object ID
    val exerciseId: Int, // Exercise object ID
    val repetitions: Int, // Exercise repetitions in one set
    val weight: Float? = null // Weight used in exercise
)
