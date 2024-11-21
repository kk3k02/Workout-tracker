package com.kk3k.workouttracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ViewModel class for managing workout-related data and operations
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    // Initializing DAOs for accessing workout, series, and exercise data in the database
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val seriesDao = AppDatabase.getDatabase(application).seriesDao()
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // Flow that emits all finished workouts
    val finishedWorkouts: Flow<List<Workout>> = workoutDao.getFinishedWorkouts()

    // Method to insert a new workout into the database
    fun insertWorkout(workout: Workout) {
        // Launches a coroutine to perform database insertion asynchronously
        viewModelScope.launch {
            workoutDao.insert(workout)  // Insert workout into the database
        }
    }

    // Method to insert a new series (set of repetitions) into the database
    fun insertSeries(series: Series) {
        // Launches a coroutine to insert the series asynchronously
        viewModelScope.launch {
            seriesDao.insert(series)  // Insert series into the database
        }
    }

    // Method to delete a specific series from the database
    fun deleteSeries(series: Series) {
        // Launches a coroutine to delete the series asynchronously
        viewModelScope.launch {
            seriesDao.delete(series)  // Delete the series from the database
        }
    }

    // Method to delete all workouts from the database
    fun deleteAllWorkouts() {
        // Launches a coroutine to delete all workouts asynchronously
        viewModelScope.launch {
            workoutDao.deleteAll()  // Delete all workouts from the database
        }
    }

    // Method to delete a specific workout from the database
    fun deleteWorkout(workout: Workout) {
        // Launches a coroutine to delete the workout asynchronously
        viewModelScope.launch {
            workoutDao.delete(workout)  // Delete the workout from the database
        }
    }

    // Method to retrieve the most recent workout from the database and pass it to a callback
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        // Launches a coroutine to fetch the most recent workout asynchronously
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()  // Fetch the most recent workout
            callback(workout)  // Pass the workout (or null if not found) to the callback
        }
    }

    // Method to retrieve an unfinished workout from the database
    suspend fun getUnfinishedWorkout(): Workout? {
        return workoutDao.getUnfinishedWorkout()  // Fetch the unfinished workout (if any)
    }

    // Method to retrieve exercises associated with a specific workout ID
    suspend fun getExercisesForWorkout(workoutId: Int): List<Exercise> {
        // Fetch the exercise IDs associated with the workout
        val exerciseIds = seriesDao.getExerciseIdsForWorkout(workoutId)
        // Fetch the exercises using the exercise IDs
        return exerciseDao.getExercisesByIds(exerciseIds)
    }

    // Method to retrieve all series (sets) for a specific exercise within a workout
    suspend fun getSeriesForExercise(workoutId: Int, exerciseId: Int): List<Series> {
        // Fetch all series for the given exercise ID within the specified workout
        return seriesDao.getSeriesForWorkoutAndExercise(workoutId, exerciseId)
    }

    // Method to set workout details such as duration, notes, and mark the workout as finished
    fun setWorkoutDetails(workoutId: Int, duration: Long, note: String) {
        // Launch a coroutine to update workout details asynchronously
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)  // Retrieve the workout by its ID
            workout?.let {
                // Set the workout duration, notes, and mark it as finished
                it.duration = duration
                it.notes = note
                it.isFinished = true
                workoutDao.update(it)  // Update the workout in the database
            }
        }
    }
}