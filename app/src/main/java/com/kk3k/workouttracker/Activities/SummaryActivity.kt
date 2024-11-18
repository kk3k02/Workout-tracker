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
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary)

        // Find buttons by their IDs
        val buttonBodyStatistics: Button = findViewById(R.id.buttonBodyStatistics)
        val buttonWorkoutStatistics: Button = findViewById(R.id.buttonWorkoutStatistics)

        // Set click listeners for the buttons
        buttonBodyStatistics.setOnClickListener {
            val intent = Intent(this, BodyStatisticsActivity::class.java)
            startActivity(intent)
        }

        buttonWorkoutStatistics.setOnClickListener {
            val intent = Intent(this, WorkoutStatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}
