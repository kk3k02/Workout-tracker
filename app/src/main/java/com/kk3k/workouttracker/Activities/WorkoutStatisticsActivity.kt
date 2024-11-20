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
import kotlinx.coroutines.launch

class WorkoutStatisticsActivity : AppCompatActivity() {

    private lateinit var buttonGeneral: Button
    private lateinit var buttonProgressChart: Button
    private lateinit var chartContainer: FrameLayout
    private lateinit var workoutDao: WorkoutDao  // Declare workoutDao as lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonGeneral = findViewById(R.id.btnGeneral)
        buttonProgressChart = findViewById(R.id.btnProgressChart)
        chartContainer = findViewById(R.id.chartContainer)

        // Initialize workoutDao here (make sure your AppDatabase is set up correctly)
        val workoutDatabase = AppDatabase.getDatabase(this)  // Get the database instance
        workoutDao = workoutDatabase.workoutDao()  // Initialize workoutDao

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

            val generalTextView = TextView(this@WorkoutStatisticsActivity).apply {
                textSize = 18f
                text = "Number of completed workouts: $workoutCount"
            }

            chartContainer.addView(generalTextView)  // Add content to container
        }
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
