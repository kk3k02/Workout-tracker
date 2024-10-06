package com.kk3k.workouttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "bodyMeasurement") // Table name
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Object ID
    val date: Date, // Date of measurement
    val weight: Int? = null, // User weight
    val biceps: Int? = null, // Biceps size
    val triceps: Int? = null, // Triceps size
    val chest: Int? = null, // Chest size
    val waist: Int? = null, // Waist size
    val hips: Int? = null, // Hips size
    val thighs: Int? = null, // Thighs size
    val calves: Int? = null, // Calves size
    val notes: String? = null // Measurement notes
)
