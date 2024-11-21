package com.kk3k.workouttracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kk3k.workouttracker.db.dao.BodyMeasurementDao
import com.kk3k.workouttracker.db.dao.ExerciseDao
import com.kk3k.workouttracker.db.dao.SeriesDao
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout

// Room Database class to manage all the database entities and DAO operations
@Database(
    entities = [  // List of entities that will be part of the database
        Workout::class,         // Workout entity
        BodyMeasurement::class, // BodyMeasurement entity
        Exercise::class,        // Exercise entity
        Series::class           // Series entity
    ],
    version = 4 // Incremented version number for the database schema update
)
@TypeConverters(Converters::class)  // Use TypeConverters to handle complex types in Room (e.g., date or custom types)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods to access the DAOs (Data Access Objects) for each entity
    abstract fun workoutDao(): WorkoutDao  // Provides access to workout-related data
    abstract fun bodyMeasurementDao(): BodyMeasurementDao  // Provides access to body measurement-related data
    abstract fun exerciseDao(): ExerciseDao  // Provides access to exercise-related data
    abstract fun seriesDao(): SeriesDao  // Provides access to series (sets of exercises) data

    companion object {
        // Singleton pattern to ensure only one instance of the database exists
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Get the instance of the database, creating it if necessary
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // If no instance exists, create a new one
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,  // Database class
                    "workout_tracker_database"  // Database name
                )
                    .build()  // Build the database instance
                INSTANCE = instance  // Store the instance for future use
                instance  // Return the instance
            }
        }
    }
}