package com.kk3k.workouttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import kotlin.time.Duration

@Entity(tableName = "workout") // Table name
data class Workout(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Object ID
    val date: Date, // Date of workout
    val duration: Duration, // Workout duration
    val notes: String // Notes about workout
)
