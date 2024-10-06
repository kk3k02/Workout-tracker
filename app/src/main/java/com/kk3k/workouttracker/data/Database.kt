package com.kk3k.workouttracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    // List of entities that will be managed by database
    entities = [
        BodyMeasurement::class, // Body Measurement entity
        Workout::class, // Workout entity
        Set::class, // Set entity
        Exercise::class // Exercise entity
               ],
    version = 1
)
abstract class Database: RoomDatabase() {
    // Abstract method to access the BodyMeasurement DAO
    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    // Abstract method to access the Workout DAO
    abstract fun workoutDao(): WorkoutDao

    // Abstract method to access the Set DAO
    abstract fun setDao(): SetDao

    // Abstract method to access the Exercise DAO
    abstract fun exerciseDao(): ExerciseDao
}