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

class _SetRepository(private val _setDao: _SetDao) {
    val allSets: Flow<List<_Set>> = _setDao.getAllSets()

    suspend fun insert(set: _Set) {
        _setDao.insert(set)
    }

    suspend fun delete(set: _Set) {
        _setDao.delete(set)
    }

    suspend fun update(set: _Set) {
        _setDao.update(set)
    }

    suspend fun dropSets() {
        _setDao.dropSets()
    }
}