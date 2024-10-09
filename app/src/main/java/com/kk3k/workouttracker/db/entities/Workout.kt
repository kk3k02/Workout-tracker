package com.kk3k.workouttracker.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout") // Table name
data class Workout(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0, // Object ID
    @ColumnInfo(name = "date") var date: Long? = null, // Date of workout
    @ColumnInfo(name = "duration") var duration: Long? = null, // Workout duration
    @ColumnInfo(name = "notes") var notes: String? = null // Notes about workout
)
