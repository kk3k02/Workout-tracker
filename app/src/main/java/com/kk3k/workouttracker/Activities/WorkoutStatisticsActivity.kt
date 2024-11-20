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

    private lateinit var buttonGeneral: Button
    private lateinit var buttonProgressChart: Button
    private lateinit var chartContainer: FrameLayout
    private lateinit var workoutDao: WorkoutDao
    private lateinit var seriesDao: SeriesDao  // Declare SeriesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonGeneral = findViewById(R.id.btnGeneral)
        buttonProgressChart = findViewById(R.id.btnProgressChart)
        chartContainer = findViewById(R.id.chartContainer)

        // Initialize workoutDao and seriesDao
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()  // Initialize SeriesDao

        // Set the "General" button as default active button
        deactivateButtons()
        buttonGeneral.isEnabled = false

        // Set click listeners for the buttons
        buttonGeneral.setOnClickListener {
            deactivateButtons()
            buttonGeneral.isEnabled = false
            chartContainer.removeAllViews()  // Clear the container
            showGeneralContent()  // Display general content
        }

        buttonProgressChart.setOnClickListener {
            deactivateButtons()
            buttonProgressChart.isEnabled = false
            chartContainer.removeAllViews()  // Clear the container
            showProgressChartContent()  // Display progress chart content
        }

        // Initially show the general content
        showGeneralContent()
    }

    // Deactivate all buttons (make them clickable again)
    private fun deactivateButtons() {
        buttonGeneral.isEnabled = true
        buttonProgressChart.isEnabled = true
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

            val generalTextView = TextView(this@WorkoutStatisticsActivity).apply {
                textSize = 18f
                text = """
                    Number of completed workouts: $workoutCount
                    Total duration of all workouts: ${formatDuration(totalDuration)}
                    Total weight used: ${formatWeight(totalWeight)}
                """
            }

            chartContainer.addView(generalTextView)  // Add content to container
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

    // Show progress chart content in the container
    private fun showProgressChartContent() {
        val progressChartTextView = TextView(this).apply {
            textSize = 18f
            text = "This is the progress chart content. You can add a chart here."
        }

        chartContainer.addView(progressChartTextView)  // Add content to container
    }
}
