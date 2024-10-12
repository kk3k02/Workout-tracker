package com.kk3k.workouttracker.viewmodel

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

    // Get all workouts as Flow
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkoutsFlow()

    // Insert a new workout
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

    // Metoda w WorkoutViewModel do usuwania serii
    fun deleteSeries(series: Series) {
        viewModelScope.launch {
            seriesDao.delete(series)
        }
    }


    // Nowa funkcja: tworzy trening z ćwiczeniami i seriami
    fun createWorkoutWithExercisesAndSeries(
        exercise: Exercise, // Ćwiczenie, które ma być dodane do treningu
        seriesList: List<Pair<Int, Float?>> // Lista par: (powtórzenia, obciążenie)
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // 1. Tworzenie nowego treningu
                val newWorkout = Workout(
                    date = System.currentTimeMillis(),  // Bieżąca data
                    duration = null,                    // Czas trwania treningu
                    notes = "New workout with ${exercise.name}"
                )
                // Wstaw trening do bazy danych
                workoutDao.insert(newWorkout)

                // 2. Pobierz najnowszy trening z bazy danych (aby uzyskać jego ID)
                val recentWorkout = workoutDao.getMostRecentWorkout()

                recentWorkout?.let { workout ->
                    // 3. Dodanie serii do ćwiczenia w ramach treningu
                    for ((repetitions, weight) in seriesList) {
                        val newSeries = Series(
                            workoutId = workout.uid,    // ID treningu
                            exerciseId = exercise.uid,  // ID ćwiczenia
                            repetitions = repetitions,  // Powtórzenia
                            weight = weight             // Waga
                        )
                        // Wstaw nową serię do bazy danych
                        seriesDao.insert(newSeries)
                    }
                }
            }
        }
    }

    // Sample workouts for testing purposes
    fun insertSampleWorkouts() {
        viewModelScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = 60L,
                notes = "Sample workout with multiple exercises"
            )

            workoutDao.insert(workout)

            val createdWorkout = workoutDao.getMostRecentWorkout()

            createdWorkout?.let { workout ->
                val workoutId = workout.uid

                val exerciseIds = listOf(1, 2, 3, 4, 5)

                // Pobieranie ćwiczeń z bazy danych na podstawie ich UIDs
                val exerciseList = exerciseIds.mapNotNull { exerciseId ->
                    exerciseDao.getExerciseById(exerciseId)
                }

                // Przykładowe serie dla każdego ćwiczenia
                val seriesData = mapOf(
                    1 to listOf(Pair(10, 60f), Pair(8, 65f), Pair(6, 70f)), // Bench Press
                    2 to listOf(Pair(12, 100f), Pair(10, 110f), Pair(8, 120f)), // Squat
                    3 to listOf(Pair(8, 140f), Pair(6, 150f)), // Deadlift
                    4 to listOf(Pair(12, null), Pair(10, null)), // Pull-up
                    5 to listOf(Pair(15, 20f), Pair(12, 25f)) // Bicep Curl
                )

                // Dodajemy serie do każdego ćwiczenia
                for (exercise in exerciseList) {
                    val exerciseId = exercise.uid

                    // Pobieramy serie dla ćwiczenia
                    seriesData[exerciseId]?.let { seriesList ->
                        for ((reps, weight) in seriesList) {
                            val series = Series(
                                workoutId = workoutId,
                                exerciseId = exerciseId,
                                repetitions = reps,
                                weight = weight
                            )
                            // Wstawiamy serie do bazy danych
                            seriesDao.insert(series)
                        }
                    }
                }
            }
        }
    }

    // Delete all workouts
    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
        }
    }

    // Update an existing workout
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

    // Delete a specific workout
    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }

    // Get a specific workout by ID
    fun getWorkoutById(id: Int, callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutById(id)
            callback(workout)
        }
    }

    // Get workout count
    fun getWorkoutCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = workoutDao.getWorkoutCount()
            callback(count)
        }
    }

    // Get the most recent workout
    fun getMostRecentWorkout(callback: (Workout?) -> Unit) {
        viewModelScope.launch {
            val workout = workoutDao.getMostRecentWorkout()
            callback(workout)
        }
    }
}
