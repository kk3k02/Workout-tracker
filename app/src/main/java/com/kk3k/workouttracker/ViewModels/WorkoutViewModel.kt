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
    // DAOs initialization
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val seriesDao = AppDatabase.getDatabase(application).seriesDao()
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    // LiveData/Flow properties to observe workouts in UI
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsFlow()
    val finishedWorkouts: Flow<List<Workout>> = workoutDao.getFinishedWorkouts()

    // Method to insert a new workout into the database
    fun insertWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    // Method to insert a new series entry into the database
    fun insertSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.insert(series)
        }
    }

    // Method to delete a specific series from the database
    fun deleteSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.delete(series)
        }
    }

    // Method to create a new workout entry and associate exercises and series to it
    fun createWorkoutWithExercisesAndSeries(
        exercise: Exercise,
        seriesList: List<Pair<Int, Float?>>
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newWorkout = Workout(
                    date = System.currentTimeMillis(),
                    duration = null,
                    notes = "New workout with ${exercise.name}"
                )
                workoutDao.insert(newWorkout) // Insert the new workout

                val recentWorkout = workoutDao.getMostRecentWorkout()
                recentWorkout?.let { workout ->
                    seriesList.forEach { (repetitions, weight) ->
                        val newSeries = Series(
                            workoutId = workout.uid,
                            exerciseId = exercise.uid,
                            repetitions = repetitions,
                            weight = weight
                        )
                        seriesDao.insert(newSeries) // Insert each series
                    }
                }
            }
        }
    }

    // Method to delete all workouts from the database
    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
        }
    }

    // Method to update a specific workout in the database
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

    // Method to delete a specific workout from the database
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }

    // Method to retrieve a workout by ID and pass it to a callback function
    fun getWorkoutById(id: Int, callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(id)
            callback(workout)
        }
    }

    // Method to retrieve the total workout count and pass it to a callback function
    fun getWorkoutCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = workoutDao.getWorkoutCount()
            callback(count)
        }
    }

    // Method to retrieve the most recent workout and pass it to a callback function
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()
            callback(workout)
        }
    }

    // Method to retrieve an unfinished workout from the database
    suspend fun getUnfinishedWorkout(): Workout? {
        return workoutDao.getUnfinishedWorkout()
    }

    // Method to retrieve exercises associated with a specific workout ID
    suspend fun getExercisesForWorkout(workoutId: Int): List<Exercise> {
        val exerciseIds = seriesDao.getExerciseIdsForWorkout(workoutId)
        return exerciseDao.getExercisesByIds(exerciseIds)
    }

    // Method to retrieve all series for a specific exercise within a workout
    suspend fun getSeriesForExercise(workoutId: Int, exerciseId: Int): List<Series> {
        return seriesDao.getSeriesForWorkoutAndExercise(workoutId, exerciseId)
    }

    // Method to set workout details such as duration and notes, and mark it as finished
    fun setWorkoutDetails(workoutId: Int, duration: Long, note: String) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(workoutId)
            workout?.let {
                it.duration = duration
                it.notes = note
                it.isFinished = true
                workoutDao.update(it)
            }
        }
    }
}
