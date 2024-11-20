package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kk3k.workouttracker.R

class WorkoutStatisticsActivity : AppCompatActivity() {

    private lateinit var buttonGeneral: Button
    private lateinit var buttonProgressChart: Button
    private lateinit var chartContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonGeneral = findViewById(R.id.btnGeneral)
        buttonProgressChart = findViewById(R.id.btnProgressChart)
        chartContainer = findViewById(R.id.chartContainer)

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
        val generalTextView = TextView(this).apply {
            textSize = 18f
            text = "This is the general content. Information goes here."
        }

        chartContainer.addView(generalTextView)  // Add content to container
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
