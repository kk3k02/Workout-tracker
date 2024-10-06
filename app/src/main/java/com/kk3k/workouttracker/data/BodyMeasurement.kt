package com.kk3k.workouttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "bodyMeasurement") // Table name
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Object ID
    val date: Date, // Date of measurement
    val weight: Int, // User weight
    val biceps: Int, // Biceps size
    val triceps: Int, // Triceps size
    val chest: Int, // Chest size
    val waist: Int, // Waist size
    val hips: Int, // Hips size
    val thighs: Int, // Thighs size
    val calves: Int, // Calves size
    val notes: String // Measurement notes
)
