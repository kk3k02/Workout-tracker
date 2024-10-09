package com.kk3k.workouttracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SeriesViewModel(application: Application) : AndroidViewModel(application) {
    private val setDao = AppDatabase.getDatabase(application).setDao()

    // Get all sets for a specific workout as Flow
    fun getSetsForWorkout(workoutId: Int): Flow<List<Series>> {
        return setDao.getSeriesForWorkout(workoutId)
    }

    // Get all sets for a specific exercise in a workout
    fun getSetsForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<Series>> {
        return setDao.getSeriesForExerciseInWorkout(workoutId, exerciseId)
    }

    // Insert a new set (series)
    fun insertSet(set: Series) {
        viewModelScope.launch {
            setDao.insert(set)
        }
    }

    // Insert multiple sets
    fun insertMultipleSets(vararg sets: Series) {
        viewModelScope.launch {
            setDao.insertAll(*sets)
        }
    }

    // Update an existing set
    fun updateSet(set: Series) {
        viewModelScope.launch {
            setDao.update(set)
        }
    }

    // Delete a specific set
    fun deleteSet(set: Series) {
        viewModelScope.launch {
            setDao.delete(set)
        }
    }

    // Delete all sets for a specific workout
    fun deleteSetsForWorkout(workoutId: Int) {
        viewModelScope.launch {
            setDao.deleteSeriesForWorkout(workoutId)
        }
    }

    // Delete all sets
    fun deleteAllSets() {
        viewModelScope.launch {
            setDao.deleteAll()
        }
    }

    // Get the count of all sets
    fun getSeriesCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = setDao.getSeriesCount()
            callback(count)
        }
    }
}
