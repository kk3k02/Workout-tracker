package com.kk3k.workouttracker.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurement") // Table name
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0, // Object ID
    @ColumnInfo(name = "date") var date: Long? = null, // Date of measurement
    @ColumnInfo(name = "weight") var weight: Int? = null, // User weight
    @ColumnInfo(name = "biceps") var biceps: Int? = null, // Biceps size
    @ColumnInfo(name = "triceps") var triceps: Int? = null, // Triceps size
    @ColumnInfo(name = "chest") var chest: Int? = null, // Chest size
    @ColumnInfo(name = "waist") var waist: Int? = null, // Waist size
    @ColumnInfo(name = "hips") var hips: Int? = null, // Hips size
    @ColumnInfo(name = "thighs") var thighs: Int? = null, // Thighs size
    @ColumnInfo(name = "calves") var calves: Int? = null, // Calves size
    @ColumnInfo(name = "notes") var notes: String? = null // Measurement notes
)
