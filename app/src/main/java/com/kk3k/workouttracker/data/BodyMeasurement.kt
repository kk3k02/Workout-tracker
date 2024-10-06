package com.kk3k.workouttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "bodyMeasurement")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val date: Date,
    val weight: Int,
    val biceps: Int,
    val triceps: Int,
    val chest: Int,
    val waist: Int,
    val hips: Int,
    val thighs: Int,
    val calves: Int,
    val notes: String
)
