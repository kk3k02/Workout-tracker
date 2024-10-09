package com.kk3k.workouttracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    // Get all workouts as Flow
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsFlow()

    // Insert a new workout
    fun insertWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
            Log.d("WorkoutLog", "Inserted workout: ${workout.uid}")
        }
    }

    // Update an existing workout
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
            Log.d("WorkoutLog", "Updated workout: ${workout.uid}")
        }
    }

    // Delete a specific workout
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
            Log.d("WorkoutLog", "Deleted workout: ${workout.uid}")
        }
    }

    // Delete all workouts
    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
            Log.d("WorkoutLog", "All workouts deleted")
        }
    }

    // Get a specific workout by ID
    fun getWorkoutById(id: Int, callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(id)
            callback(workout)
            workout?.let {
                Log.d("WorkoutLog", "Fetched workout: ${it.uid}")
            }
        }
    }

    // Get workout count
    fun getWorkoutCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = workoutDao.getWorkoutCount()
            callback(count)
            Log.d("WorkoutLog", "Workout count: $count")
        }
    }

    // Get the most recent workout
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()
            callback(workout)
            workout?.let {
                Log.d("WorkoutLog", "Most recent workout: ${it.uid}")
            }
        }
    }

    fun insertSampleWorkouts(workoutViewModel: WorkoutViewModel) {
        for (i in 1..10) {
            val workout = Workout(
                date = System.currentTimeMillis() - (i * 1000L * 60L * 60L * 24L),
                duration = (30L * i),
                notes = "Workout number $i"
            )
            workoutViewModel.insertWorkout(workout)
        }
    }
}
