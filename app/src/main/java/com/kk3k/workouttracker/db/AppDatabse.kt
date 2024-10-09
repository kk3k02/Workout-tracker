package com.kk3k.workouttracker.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.kk3k.workouttracker.db.dao.*
import com.kk3k.workouttracker.db.entities.*

@Database(
    entities = [
        Workout::class,
        BodyMeasurement::class,
        Exercise::class,
        Series::class
               ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SeriesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
