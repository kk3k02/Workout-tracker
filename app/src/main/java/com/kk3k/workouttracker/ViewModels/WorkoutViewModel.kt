package com.kk3k.workouttracker.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val seriesDao = AppDatabase.getDatabase(application).seriesDao()
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // Get all workouts as a Flow
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsFlow()
    val finishedWorkouts: Flow<List<Workout>> = workoutDao.getFinishedWorkouts()

    // Insert a new workout into the database
    fun insertWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    // Insert a new series into the database
    fun insertSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.insert(series)
        }
    }

    // Delete a specific series from the database
    fun deleteSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.delete(series)
        }
    }

    // Creates a new workout and adds exercises and series to it
    fun createWorkoutWithExercisesAndSeries(
        exercise: Exercise,  // Exercise to add to the workout
        seriesList: List<Pair<Int, Float?>>  // List of pairs: (repetitions, weight)
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // 1. Create a new workout with current timestamp
                val newWorkout = Workout(
                    date = System.currentTimeMillis(),
                    duration = null,
                    notes = "New workout with ${exercise.name}"
                )
                workoutDao.insert(newWorkout)  // Insert workout into the database

                // 2. Get the most recent workout to obtain its ID
                val recentWorkout = workoutDao.getMostRecentWorkout()

                recentWorkout?.let { workout ->
                    // 3. Add series to the exercise within the workout
                    for ((repetitions, weight) in seriesList) {
                        val newSeries = Series(
                            workoutId = workout.uid,
                            exerciseId = exercise.uid,
                            repetitions = repetitions,
                            weight = weight
                        )
                        seriesDao.insert(newSeries)  // Insert each series into the database
                    }
                }
            }
        }
    }

    // Deletes all workouts from the database
    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
        }
    }

    // Updates an existing workout in the database
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

    // Deletes a specific workout
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }

    // Retrieve a workout by its ID, using a callback
    fun getWorkoutById(id: Int, callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(id)
            callback(workout)
        }
    }

    // Retrieve the total count of workouts, using a callback
    fun getWorkoutCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = workoutDao.getWorkoutCount()
            callback(count)
        }
    }

    // Retrieve the most recent workout, using a callback
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()
            callback(workout)
        }
    }

    // Mark a workout as finished by setting its isFinished attribute to true
    fun markWorkoutAsFinished(workoutId: Int) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                it.isFinished = true
                workoutDao.update(it)
            }
        }
    }

    // Get an unfinished workout (if any) from the database
    suspend fun getUnfinishedWorkout(): Workout? {
        return workoutDao.getUnfinishedWorkout()
    }

    // Retrieve exercises associated with a specific workout ID
    suspend fun getExercisesForWorkout(workoutId: Int): List<Exercise> {
        val exerciseIds = seriesDao.getExerciseIdsForWorkout(workoutId)  // Get unique exercise IDs for the workout
        return exerciseDao.getExercisesByIds(exerciseIds)  // Fetch exercises by these IDs
    }

    // Retrieve series for a specific exercise within a workout
    suspend fun getSeriesForExercise(workoutId: Int, exerciseId: Int): List<Series> {
        return seriesDao.getSeriesForWorkoutAndExercise(workoutId, exerciseId)
    }

    // Function to set and save workout duration in the database
    fun setWorkoutDuration(workoutId: Int, duration: Long) {
        viewModelScope.launch {
            Log.d("DURATION", "DURATION IN VIEWMODEL IS $duration")
            workoutDao.updateDuration(workoutId, duration)  // Update duration field in database
            Log.d("DURATION", "DURATION SAVED IN DATABASE FOR WORKOUT ID: $workoutId")
        }
    }
}
