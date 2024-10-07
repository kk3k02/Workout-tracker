package com.kk3k.workouttracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.db.entities._Set

class DatabaseViewModel : ViewModel() {
    val measurementDao = MainApplication.appDatabase.getMeasurementDao()
    val exerciseDao = MainApplication.appDatabase.getExerciseDao()
    val setDao = MainApplication.appDatabase.getSetDao()
    val workoutDao = MainApplication.appDatabase.getWorkoutDao()

    val getAllMeasurements: LiveData<List<BodyMeasurement>> = measurementDao.getAllMeasurements()
    val getAllExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()
    val getAllSets: LiveData<List<_Set>> = setDao.getAllSets()
    val getAllWorkouts: LiveData<List<Workout>> = workoutDao.getAllWorkouts()

    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(set: _Set) {
        setDao.insert(set)
    }

    suspend fun delete(set: _Set) {
        setDao.delete(set)
    }

    suspend fun update(set: _Set) {
        setDao.update(set)
    }

    fun getAllSets(): LiveData<List<_Set>> {
        return setDao.getAllSets()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(measurement: BodyMeasurement) {
        measurementDao.insert(measurement)
    }

    suspend fun delete(measurement: BodyMeasurement) {
        measurementDao.delete(measurement)
    }

    suspend fun update(measurement: BodyMeasurement) {
        measurementDao.update(measurement)
    }

    fun getAllMeasurements(): LiveData<List<BodyMeasurement>> {
        return measurementDao.getAllMeasurements()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    fun getAllExercises(): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(workout: Workout) {
        workoutDao.insert(workout)
    }

    suspend fun delete(workout: Workout) {
        workoutDao.delete(workout)
    }

    suspend fun update(workout: Workout) {
        workoutDao.update(workout)
    }

    fun getAllWorkouts(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
}