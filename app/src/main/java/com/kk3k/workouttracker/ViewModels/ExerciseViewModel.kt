package com.kk3k.workouttracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // Get all exercises as Flow
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercisesFlow()

    // Insert a new exercise
    fun insertExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.insert(exercise)
        }
    }

    // Update an existing exercise
    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.update(exercise)
        }
    }

    // Delete a specific exercise
    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.delete(exercise)
        }
    }

    // Delete all exercises
    fun deleteAllExercises() {
        viewModelScope.launch {
            exerciseDao.deleteAll()
        }
    }

    // Get a specific exercise by ID
    fun getExerciseById(id: Int, callback: (Exercise?) -> Unit) {
        viewModelScope.launch {
            val exercise = exerciseDao.getExerciseById(id)
            callback(exercise)
        }
    }

    // Get exercises by name (case-insensitive)
    fun getExercisesByName(name: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByName(name)
    }

    // Get exercises by target muscle group
    fun getExercisesByTargetMuscle(targetMuscle: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByTargetMuscle(targetMuscle)
    }

    // Get the count of all exercises
    fun getExerciseCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = exerciseDao.getExerciseCount()
            callback(count)
        }
    }
}
