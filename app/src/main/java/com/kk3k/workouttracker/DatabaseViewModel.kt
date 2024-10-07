package com.kk3k.workouttracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.db.entities._Set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        viewModelScope.launch(Dispatchers.IO) {
            setDao.insert(set)
        }
    }

    suspend fun delete(set: _Set) {
        viewModelScope.launch(Dispatchers.IO) {
            setDao.delete(set)
        }
    }

    suspend fun update(set: _Set) {
        viewModelScope.launch(Dispatchers.IO) {
            setDao.update(set)
        }
    }

    fun getAllSets(): LiveData<List<_Set>> {
        return setDao.getAllSets()
    }

    suspend fun dropSets() {
        viewModelScope.launch(Dispatchers.IO) {
            setDao.dropSets()
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(measurement: BodyMeasurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementDao.insert(measurement)
        }
    }

    suspend fun delete(measurement: BodyMeasurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementDao.delete(measurement)
        }
    }

    suspend fun update(measurement: BodyMeasurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementDao.update(measurement)
        }
    }

    fun getAllMeasurements(): LiveData<List<BodyMeasurement>> {
        return measurementDao.getAllMeasurements()
    }

    suspend fun dropMeasurements() {
        viewModelScope.launch(Dispatchers.IO) {
            measurementDao.dropMeasurements()
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.insert(exercise)
        }
    }

    suspend fun delete(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.delete(exercise)
        }
    }

    suspend fun update(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.update(exercise)
        }
    }

    fun getAllExercises(): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }

    suspend fun dropExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.dropExercises()
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun insert(workout: Workout) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDao.insert(workout)
        }
    }

    suspend fun delete(workout: Workout) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDao.delete(workout)
        }
    }

    suspend fun update(workout: Workout) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDao.update(workout)
        }
    }

    fun getAllWorkouts(): LiveData<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }

    suspend fun dropWorkouts() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDao.dropWorkouts()
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
}