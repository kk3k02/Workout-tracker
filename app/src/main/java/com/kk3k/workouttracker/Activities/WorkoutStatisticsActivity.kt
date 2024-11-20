package com.kk3k.workouttracker.Activities

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
import kotlinx.coroutines.launch

class WorkoutStatisticsActivity : AppCompatActivity() {

    private lateinit var buttonSelectExercise: Button
    private lateinit var chartContainer: FrameLayout
    private lateinit var workoutDao: WorkoutDao
    private lateinit var seriesDao: SeriesDao  // Declare SeriesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonSelectExercise = findViewById(R.id.btnSelectExercise)
        chartContainer = findViewById(R.id.chartContainer)

        // Initialize workoutDao and seriesDao
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()  // Initialize SeriesDao

        // Set click listener for the "Select Exercise" button
        buttonSelectExercise.setOnClickListener {
            // Placeholder action to show exercise selection logic
            // You can add a dialog or another activity to allow users to select an exercise.
            showExerciseSelection()
        }

        // Initially show the general content
        showGeneralContent()
    }

    // Show general content in the container
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

            workoutCountTextView.text = "Number of completed workouts: $workoutCount"
            totalDurationTextView.text = "Total duration of all workouts: ${formatDuration(totalDuration)}"
            totalWeightTextView.text = "Total weight used: ${formatWeight(totalWeight)}"
        }
    }

    // Format duration in milliseconds into a human-readable format (HH:mm:ss)
    private fun formatDuration(durationInMillis: Long): String {
        val hours = (durationInMillis / 3600000) % 24
        val minutes = (durationInMillis / 60000) % 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Format weight into a human-readable format (e.g., kg)
    private fun formatWeight(weight: Float): String {
        return String.format("%.2f kg", weight)
    }

    // Placeholder function to simulate exercise selection
    private fun showExerciseSelection() {
        // You can open a dialog or a new activity here to allow the user to select an exercise.
        // For now, let's just show a simple placeholder text.
        val exerciseSelectionTextView = TextView(this).apply {
            text = "Exercise selection feature coming soon!"
        }
        chartContainer.removeAllViews()  // Clear the container
        chartContainer.addView(exerciseSelectionTextView)  // Add the placeholder text
    }
}
