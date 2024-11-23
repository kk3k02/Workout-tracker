package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kk3k.workouttracker.R

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide the ActionBar

        // Enable edge-to-edge display, making the app use the full screen area
        enableEdgeToEdge()

        // Set the layout for this activity from the XML resource
        setContentView(R.layout.activity_summary)

        // Find the buttons by their respective IDs in the layout
        val buttonBodyStatistics: Button = findViewById(R.id.buttonBodyStatistics)
        val buttonWorkoutStatistics: Button = findViewById(R.id.buttonWorkoutStatistics)

        // Set an on-click listener for the "Body Statistics" button
        buttonBodyStatistics.setOnClickListener {
            // When clicked, navigate to the BodyStatisticsActivity
            val intent = Intent(this, BodyStatisticsActivity::class.java)
            startActivity(intent)  // Start the new activity
        }

        // Set an on-click listener for the "Workout Statistics" button
        buttonWorkoutStatistics.setOnClickListener {
            // When clicked, navigate to the WorkoutStatisticsActivity
            val intent = Intent(this, WorkoutStatisticsActivity::class.java)
            startActivity(intent)  // Start the new activity
        }
    }
}