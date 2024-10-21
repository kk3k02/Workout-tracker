package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.ExerciseLoader
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

        ExerciseLoader.loadExercisesIfNeeded(this, exerciseViewModel)

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

        lifecycleScope.launch {
            //workoutViewModel.deleteAllWorkouts()
            //exerciseViewModel.deleteAllExercises()
            //insertSampleData()
            //logWorkout()
            //logExercises()
            //logSeries()
            //logBodyMeasurements()
            //workoutViewModel.deleteAllWorkouts()
        }
    }

    private fun cleanData() {
        workoutViewModel.deleteAllWorkouts()
        exerciseViewModel.deleteAllExercises()
        seriesViewModel.deleteAllSeries()
        bodyMeasurementViewModel.deleteAllMeasurements()
    }

}
