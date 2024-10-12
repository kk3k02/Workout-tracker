package com.kk3k.workouttracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SeriesViewModel(application: Application) : AndroidViewModel(application) {
    private val seriesDao = AppDatabase.getDatabase(application).seriesDao()
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // Fetch series for a specific workout with exercise details
    fun getSeriesForWorkout(workoutId: Int): Flow<List<Pair<Exercise, List<Series>>>> {
        return seriesDao.getSeriesForWorkout(workoutId).map { seriesList ->
            // Pobierz unikalne identyfikatory ćwiczeń
            val exerciseIds = seriesList.map { it.exerciseId }.distinct()

            // Pobierz ćwiczenia dla tych identyfikatorów
            val exercises = exerciseIds.mapNotNull { exerciseId ->
                exerciseDao.getExerciseById(exerciseId)
            }

            // Powiąż ćwiczenia z odpowiadającymi im seriami
            exercises.map { exercise ->
                exercise to seriesList.filter { it.exerciseId == exercise.uid }
            }
        }
    }

    suspend fun getExercisesForWorkout(workoutId: Int): List<Exercise> {
        // Pobieramy listę unikalnych exercise_id z tabeli Series dla danego workout_id
        val seriesList = seriesDao.getSeriesForWorkout(workoutId).first()

        // Wyciągamy unikalne identyfikatory ćwiczeń
        val exerciseIds = seriesList.map { it.exerciseId }.distinct()

        // Pobieramy ćwiczenia z ExerciseDao na podstawie exercise_id
        val exercises = exerciseIds.mapNotNull { exerciseId ->
            exerciseDao.getExerciseById(exerciseId)
        }

        return exercises
    }

    // Get all sets for a specific exercise in a workout
    fun getSeriesForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<Series>> {
        return seriesDao.getSeriesForExerciseInWorkout(workoutId, exerciseId)
    }

    // Insert a new set (series)
    fun insertSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.insert(series)
        }
    }

    // Insert multiple sets
    fun insertMultipleSeries(vararg sets: Series) {
        viewModelScope.launch {
            seriesDao.insertAll(*sets)
        }
    }

    // Update an existing set
    fun updateSeries(set: Series) {
        viewModelScope.launch {
            seriesDao.update(set)
        }
    }

    // Delete a specific set
    fun deleteSeries(set: Series) {
        viewModelScope.launch {
            seriesDao.delete(set)
        }
    }

    // Delete all sets for a specific workout
    fun deleteSeriesForWorkout(workoutId: Int) {
        viewModelScope.launch {
            seriesDao.deleteSeriesForWorkout(workoutId)
        }
    }

    // Delete all sets
    fun deleteAllSeries() {
        viewModelScope.launch {
            seriesDao.deleteAll()
        }
    }

    // Get the count of all sets
    fun getSeriesCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = seriesDao.getSeriesCount()
            callback(count)
        }
    }

    fun insertSampleSeries(seriesViewModel: SeriesViewModel) {
        for (i in 1..10) {
            val series = Series(
                workoutId = i,
                exerciseId = i,
                repetitions = 8 + (i % 5),
                weight = (i * 5).toFloat()
            )
            seriesViewModel.insertSeries(series)
        }
    }
}
