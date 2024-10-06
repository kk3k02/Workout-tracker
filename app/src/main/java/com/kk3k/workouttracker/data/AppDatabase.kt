package com.kk3k.workouttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
abstract class AppDatabase: RoomDatabase() {
    // Abstract method to access the BodyMeasurement DAO
    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    // Abstract method to access the Workout DAO
    abstract fun workoutDao(): WorkoutDao

    // Abstract method to access the Set DAO
    abstract fun setDao(): SetDao

    // Abstract method to access the Exercise DAO
    abstract fun exerciseDao(): ExerciseDao
}

object Db {
    // Private nullable variable that holds the instance of the database
    private var db: AppDatabase? = null

    // Function that returns an instance of the AppDatabase
    fun getInstance(context: Context): AppDatabase {
        // If null function will create a new instance of database
        if (db == null) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "Workout-tracker-database"
            ).build()
        }
        return db!!
    }
}
