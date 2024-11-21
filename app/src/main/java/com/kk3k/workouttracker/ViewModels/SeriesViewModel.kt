package com.kk3k.workouttracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// ViewModel for managing series (sets) of exercises associated with workouts
class SeriesViewModel(application: Application) : AndroidViewModel(application) {
    private val seriesDao = AppDatabase.getDatabase(application).seriesDao()  // DAO for series (sets)
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()  // DAO for exercises

    // Function to fetch series for a specific workout along with exercise details
    fun getSeriesForWorkout(workoutId: Int): Flow<List<Pair<Exercise, List<Series>>>> {
        return seriesDao.getSeriesForWorkout(workoutId).map { seriesList ->
            // Get unique exercise IDs from the series
            val exerciseIds = seriesList.map { it.exerciseId }.distinct()

            // Retrieve exercises corresponding to the unique exercise IDs
            val exercises = exerciseIds.mapNotNull { exerciseId ->
                exerciseDao.getExerciseById(exerciseId)  // Fetch exercise by ID
            }

            // Pair each exercise with its corresponding series (sets)
            exercises.map { exercise ->
                exercise to seriesList.filter { it.exerciseId == exercise.uid }  // Match exercise with its series
            }
        }
    }

    // Function to delete all series from the database
    fun deleteAllSeries() {
        viewModelScope.launch {
            // Launch a coroutine to delete all series in the background
            seriesDao.deleteAll()  // Delete all series using the DAO
        }
    }

}