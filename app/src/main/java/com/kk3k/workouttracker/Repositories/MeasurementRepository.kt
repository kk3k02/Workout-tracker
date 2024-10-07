package com.kk3k.workouttracker

import com.kk3k.workouttracker.db.dao.MeasurementDao
import com.kk3k.workouttracker.db.dao.ExerciseDao
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.dao._SetDao
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.db.entities._Set
import kotlinx.coroutines.flow.Flow

class MeasurementRepository(private val measurementDao: MeasurementDao) {
    val allMeasurements: Flow<List<BodyMeasurement>> = measurementDao.getAllMeasurements()

    suspend fun insert(measurement: BodyMeasurement) {
        measurementDao.insert(measurement)
    }

    suspend fun delete(measurement: BodyMeasurement) {
        measurementDao.delete(measurement)
    }

    suspend fun update(measurement: BodyMeasurement) {
        measurementDao.update(measurement)
    }

    suspend fun dropMeasurements() {
        measurementDao.dropMeasurements()
    }
}