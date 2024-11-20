package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.dao.SeriesDao
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.TargetMuscle
import kotlinx.coroutines.launch
import android.app.AlertDialog
import androidx.activity.viewModels
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel

class WorkoutStatisticsActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var buttonSelectExercise: Button  // Button to select an exercise
    private lateinit var chartContainer: FrameLayout  // FrameLayout to display content
    private lateinit var workoutDao: WorkoutDao  // DAO for workout data
    private lateinit var seriesDao: SeriesDao  // DAO for series data

    // Declare viewModel to interact with exercise data
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Declare TextView to display the selected exercise
    private lateinit var selectedExerciseTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonSelectExercise = findViewById(R.id.btnSelectExercise)
        chartContainer = findViewById(R.id.chartContainer)

        // Initialize workoutDao and seriesDao to access the database
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()  // Initialize SeriesDao

        // Initialize the TextView for displaying selected exercise name
        selectedExerciseTextView = findViewById(R.id.tvSelectedExercise)

        // Set click listener for the "Select Exercise" button
        buttonSelectExercise.setOnClickListener {
            // Show muscle group selection dialog
            showMuscleGroupSelectionDialog()
        }

        // Initially show the general content
        showGeneralContent()
    }

    // Show general content in the container (e.g., total workouts, duration, weight)
    @SuppressLint("SetTextI18n")
    private fun showGeneralContent() {
        lifecycleScope.launch {
            // Get the count of finished workouts from the database
            val workoutCount = workoutDao.getFinishedWorkoutCount()

            // Get the total duration of all finished workouts
            val totalDuration = workoutDao.getTotalWorkoutDuration() ?: 0L

            // Get the total weight used in all series (weight * repetitions)
            val totalWeight = seriesDao.getTotalWeightUsed() ?: 0f

            // Find the TextView elements by ID and update them with the data
            val workoutCountTextView = findViewById<TextView>(R.id.tvWorkoutCount)
            val totalDurationTextView = findViewById<TextView>(R.id.tvTotalDuration)
            val totalWeightTextView = findViewById<TextView>(R.id.tvTotalWeight)

            // Update UI with fetched statistics
            workoutCountTextView.text = "Number of completed workouts: $workoutCount"
            totalDurationTextView.text = "Total duration of all workouts: ${formatDuration(totalDuration)}"
            totalWeightTextView.text = "Total weight used: ${formatWeight(totalWeight)}"
        }
    }

    // Format duration in milliseconds into a human-readable format (HH:mm:ss)
    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationInMillis: Long): String {
        val hours = (durationInMillis / 3600000) % 24
        val minutes = (durationInMillis / 60000) % 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Format weight into a human-readable format (e.g., kg)
    @SuppressLint("DefaultLocale")
    private fun formatWeight(weight: Float): String {
        return String.format("%.2f kg", weight)
    }

    // Show muscle group selection dialog
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            // When a muscle group is selected, show the exercise selection dialog
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Show exercise selection dialog based on selected muscle group
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            // Collect all exercises from the exercise view model
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                // Filter exercises based on selected muscle group
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                val exerciseNames = exerciseList.map { it.name }.toTypedArray()
                val builder = AlertDialog.Builder(this@WorkoutStatisticsActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    // Instead of updating the button, display the selected exercise below the button
                    selectedExerciseTextView.text = "Selected Exercise: ${selectedExercise.name}"
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }
}
