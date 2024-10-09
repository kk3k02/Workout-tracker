package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.viewmodel.BodyMeasurementViewModel
import com.kk3k.workouttracker.viewmodel.SeriesViewModel
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // Initialize ViewModels
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnWorkout = findViewById<Button>(R.id.btnWorkout)
        val btnWorkoutHistory = findViewById<Button>(R.id.btnWorkoutHistory)
        val btnBodyMeasurement = findViewById<Button>(R.id.btnBodyMeasurement)
        val btnSummary = findViewById<Button>(R.id.btnSummary)

        btnWorkout.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        btnWorkoutHistory.setOnClickListener {
            val intent = Intent(this, WorkoutHistoryActivity::class.java)
            startActivity(intent)
        }

        btnBodyMeasurement.setOnClickListener {
            val intent = Intent(this, BodyMeasurementActivity::class.java)
            startActivity(intent)
        }

        btnSummary.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

//        lifecycleScope.launch {
//            //insertSampleData()
//            //logWorkout()
//            //logExercises()
//            //logSeries()
//            //logBodyMeasurements()
//        }
    }

    private fun insertSampleData() {
        workoutViewModel.insertSampleWorkouts(workoutViewModel)
        exerciseViewModel.insertSampleExercises(exerciseViewModel)
        seriesViewModel.insertSampleSeries(seriesViewModel)
        bodyMeasurementViewModel.insertSampleBodyMeasurements(bodyMeasurementViewModel)
    }

    private fun cleanData() {
        workoutViewModel.deleteAllWorkouts()
        exerciseViewModel.deleteAllExercises()
        seriesViewModel.deleteAllSeries()
        bodyMeasurementViewModel.deleteAllMeasurements()
    }

    private suspend fun logWorkout() {
        workoutViewModel.allWorkouts.collect { workouts ->
            if (workouts.isEmpty()) {
                Log.d("WorkoutLog", "No workouts found in the database")
            } else {
                workouts.forEach { workout ->
                    Log.d("WorkoutLog", "Workout: ${workout.uid}, ${workout.notes}")
                }
            }
        }
    }

    private suspend fun logExercises() {
        exerciseViewModel.allExercises.collect { exercises ->
            if (exercises.isEmpty()) {
                Log.d("ExerciseLog", "No exercises found in the database")
            } else {
                exercises.forEach { exercise ->
                    Log.d("ExerciseLog", "Exercise: ${exercise.uid}, ${exercise.name}, Target muscle: ${exercise.targetMuscle}")
                }
            }
        }
    }

    private suspend fun logSeries() {
        seriesViewModel.getSetsForWorkout(1).collect { seriesList ->
            if (seriesList.isEmpty()) {
                Log.d("SeriesLog", "No series found in the database")
            } else {
                seriesList.forEach { series ->
                    Log.d("SeriesLog", "Series: ${series.uid}, Workout ID: ${series.workoutId}, Exercise ID: ${series.exerciseId}, Repetitions: ${series.repetitions}, Weight: ${series.weight}")
                }
            }
        }
    }

    private suspend fun logBodyMeasurements() {
        bodyMeasurementViewModel.allMeasurements.collect { measurements ->
            if (measurements.isEmpty()) {
                Log.d("BodyMeasurementLog", "No body measurements found in the database")
            } else {
                measurements.forEach { measurement ->
                    Log.d("BodyMeasurementLog", "BodyMeasurement: ${measurement.uid}, Weight: ${measurement.weight}, Biceps: ${measurement.biceps}, Chest: ${measurement.chest}")
                }
            }
        }
    }

}
