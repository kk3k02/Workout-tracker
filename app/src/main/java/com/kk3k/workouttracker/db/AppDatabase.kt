package com.kk3k.workouttracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kk3k.workouttracker.db.dao.BodyMeasurementDao
import com.kk3k.workouttracker.db.dao.ExerciseDao
import com.kk3k.workouttracker.db.dao._SetDao
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities._Set
import com.kk3k.workouttracker.db.entities.Workout


@Database(
    // List of entities that will be managed by database
    entities = [
        BodyMeasurement::class, // Body Measurement entity
        Workout::class, // Workout entity
        _Set::class, // Set entity
        Exercise::class // Exercise entity
               ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        const val NAME = "APP_DB"
    }

    abstract fun getMeasurementDao(): BodyMeasurementDao
    abstract fun getExerciseDao(): ExerciseDao
    abstract fun getSetDao(): _SetDao
    abstract fun getWorkoutDao(): WorkoutDao
}

