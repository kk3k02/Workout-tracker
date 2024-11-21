package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.ViewModels.WorkoutViewModel
import com.kk3k.workouttracker.db.ExerciseLoader
import com.kk3k.workouttracker.viewmodel.SeriesViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    // Initialize ViewModels for workout, exercises, series, and body measurements
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Set the layout for the activity

        // Load exercises if needed using ExerciseLoader
        ExerciseLoader.loadExercisesIfNeeded(this, exerciseViewModel)

        // Get references to the buttons from the layout
        val btnWorkout = findViewById<Button>(R.id.btnWorkout)
        val btnWorkoutHistory = findViewById<Button>(R.id.btnWorkoutHistory)
        val btnBodyMeasurement = findViewById<Button>(R.id.btnBodyMeasurement)
        val btnSummary = findViewById<Button>(R.id.btnSummary)
        val btnClose = findViewById<Button>(R.id.btnClose)

        // Set click listener for the "Workout" button
        btnWorkout.setOnClickListener {
            // Start the WorkoutActivity when clicked
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the "Workout History" button
        btnWorkoutHistory.setOnClickListener {
            // Start the WorkoutHistoryActivity when clicked
            val intent = Intent(this, WorkoutHistoryActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the "Body Measurement" button
        btnBodyMeasurement.setOnClickListener {
            // Start the BodyMeasurementActivity when clicked
            val intent = Intent(this, BodyMeasurementActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the "Summary" button
        btnSummary.setOnClickListener {
            // Start the SummaryActivity when clicked
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the "Close" button to exit the app
        btnClose.setOnClickListener {
            // Close the app by stopping all activities
            exitProcess(0)  // This will terminate the app immediately
        }

        // Launch a coroutine to handle background tasks if needed
        lifecycleScope.launch {
            // Uncomment to clean data if needed
            //workoutViewModel.deleteAllWorkouts()  // Clean all workout data
            //exerciseViewModel.deleteAllExercises()  // Clean all exercises
        }
    }

    // Clean all data (workouts, exercises, series, and body measurements)
    private fun cleanData() {
        workoutViewModel.deleteAllWorkouts()  // Delete all workout data
        exerciseViewModel.deleteAllExercises()  // Delete all exercise data
        seriesViewModel.deleteAllSeries()  // Delete all series data
        bodyMeasurementViewModel.deleteAllMeasurements()  // Delete all body measurements
    }
}
