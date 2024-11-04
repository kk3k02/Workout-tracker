package com.kk3k.workouttracker.ViewModels

import android.app.Application
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

    // Retrieves all workouts as a Flow, allowing real-time updates in UI components
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsFlow()
    val finishedWorkouts: Flow<List<Workout>> = workoutDao.getFinishedWorkouts()

    // Inserts a new workout into the database
    fun insertWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    // Inserts a new series entry into the database
    fun insertSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.insert(series)
        }
    }

    // Deletes a specific series from the database
    fun deleteSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.delete(series)
        }
    }

    // Creates a new workout entry, and associates exercises and series to it
    fun createWorkoutWithExercisesAndSeries(
        exercise: Exercise,  // The exercise to add to the workout
        seriesList: List<Pair<Int, Float?>>  // List of pairs for series (repetitions, weight)
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Step 1: Create a new workout with the current timestamp
                val newWorkout = Workout(
                    date = System.currentTimeMillis(),
                    duration = null,
                    notes = "New workout with ${exercise.name}"
                )
                workoutDao.insert(newWorkout)  // Insert the new workout into the database

                // Step 2: Retrieve the most recent workout to get its ID
                val recentWorkout = workoutDao.getMostRecentWorkout()

                recentWorkout?.let { workout ->
                    // Step 3: For each series in the list, create and add series to the workout
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

    // Updates a specific workout in the database
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

    // Deletes a specific workout from the database
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }

    // Retrieves a workout by ID and passes it to a callback function
    fun getWorkoutById(id: Int, callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(id)
            callback(workout)
        }
    }

    // Retrieves the total workout count and passes it to a callback function
    fun getWorkoutCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = workoutDao.getWorkoutCount()
            callback(count)
        }
    }

    // Retrieves the most recent workout and passes it to a callback function
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()
            callback(workout)
        }
    }

    // Marks a specific workout as finished by setting its isFinished flag to true
    fun markWorkoutAsFinished(workoutId: Int) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                it.isFinished = true
                workoutDao.update(it)
            }
        }
    }

    // Retrieves an unfinished workout from the database, if any
    suspend fun getUnfinishedWorkout(): Workout? {
        return workoutDao.getUnfinishedWorkout()
    }

    // Retrieves exercises associated with a specific workout ID
    suspend fun getExercisesForWorkout(workoutId: Int): List<Exercise> {
        // Fetch unique exercise IDs associated with the workout
        val exerciseIds = seriesDao.getExerciseIdsForWorkout(workoutId)
        // Fetch exercise details based on these IDs
        return exerciseDao.getExercisesByIds(exerciseIds)
    }

    // Retrieves all series for a specific exercise within a workout
    suspend fun getSeriesForExercise(workoutId: Int, exerciseId: Int): List<Series> {
        return seriesDao.getSeriesForWorkoutAndExercise(workoutId, exerciseId)
    }

    // Sets and saves the workout duration in the database for a specific workout
    fun setWorkoutDuration(workoutId: Int, duration: Long) {
        viewModelScope.launch {
            workoutDao.updateDuration(workoutId, duration)  // Update duration field in the database
        }
    }

    // Updates the workout notes in the database for a specific workout
    fun setWorkoutNote(workoutId: Int, note: String) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                it.notes = note
                workoutDao.update(it) // Update the workout with new notes
            }
        }
    }
}
