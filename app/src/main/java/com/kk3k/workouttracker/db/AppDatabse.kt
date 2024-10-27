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

@Database(
    entities = [
        Workout::class,
        BodyMeasurement::class,
        Exercise::class,
        Series::class
    ],
    version = 4 // Incremented to version 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun seriesDao(): SeriesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_tracker_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
