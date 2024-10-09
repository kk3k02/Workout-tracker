package com.kk3k.workouttracker.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise") // Table name
data class Exercise(
    @PrimaryKey(autoGenerate = true) var uid: Int = 0, // Object ID,
    @ColumnInfo(name = "name") var name: String, // Exercise name
    @ColumnInfo(name = "target_muscle") var targetMuscle: String, // Target muscle in exercise
    @ColumnInfo(name = "description") var description: String, // Description of exercise
    @ColumnInfo(name = "image") var image: ByteArray? = null // Exercise image
)
