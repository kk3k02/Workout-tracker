package com.kk3k.workouttracker.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "set", // Table name
    foreignKeys = [
        ForeignKey( // Foreign key in Workout
            entity = Workout::class,
            parentColumns = ["uid"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey( // Foreign key in Exercise
            entity = Exercise::class,
            parentColumns = ["uid"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class _Set(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0, // Object ID
    @ColumnInfo(name = "workout_id") var workoutId: Int, // Workout object ID
    @ColumnInfo(name = "exercise_id") var exerciseId: Int, // Exercise object ID
    @ColumnInfo(name = "repetitions") var repetitions: Int, // Exercise repetitions in one set
    @ColumnInfo(name = "weight") var weight: Float? = null // Weight used in exercise
)
