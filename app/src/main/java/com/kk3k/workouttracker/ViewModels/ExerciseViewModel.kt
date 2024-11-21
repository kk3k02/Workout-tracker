package com.kk3k.workouttracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel class for managing exercise data in the application
class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    // Access the ExerciseDao to interact with the exercise data in the database
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // Flow to observe all exercises in the database
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercisesFlow()

    // Function to insert a new exercise into the database
    fun insertExercise(exercise: Exercise) {
        // Launch a coroutine in the ViewModel scope to insert the exercise in the background
        viewModelScope.launch {
            exerciseDao.insert(exercise)  // Insert the exercise using the DAO
        }
    }

    // Function to delete all exercises from the database
    fun deleteAllExercises() {
        // Launch a coroutine in the ViewModel scope to delete all exercises in the background
        viewModelScope.launch {
            exerciseDao.deleteAll()  // Delete all exercises using the DAO
        }
    }

    // Function to get the count of all exercises in the database
    fun getExerciseCount(callback: (Int) -> Unit) {
        // Launch a coroutine in the IO dispatcher to fetch the exercise count in the background
        viewModelScope.launch(Dispatchers.IO) {
            val count = exerciseDao.getExerciseCount()  // Get the count from the DAO
            // Switch to the main thread to deliver the result back to the callback
            withContext(Dispatchers.Main) {
                callback(count)  // Return the count result to the callback function
            }
        }
    }

}