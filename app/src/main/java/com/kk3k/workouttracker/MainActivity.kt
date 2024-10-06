package com.kk3k.workouttracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.kk3k.workouttracker.databinding.ActivityMainBinding


class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Workout Button Listener
        binding.workoutButton.setOnClickListener{
            val explicitIntent = Intent(applicationContext, WorkoutActivity::class.java)
            startActivity(explicitIntent)
        }

        // Figure Button Listener
        binding.figureButton.setOnClickListener{
            val explicitIntent = Intent(applicationContext, FigureActivity::class.java)
            startActivity(explicitIntent)
        }

        // History Button Listener
        binding.historyButton.setOnClickListener{
            val explicitIntent = Intent(applicationContext, HistoryActivity::class.java)
            startActivity(explicitIntent)
        }

        // Summary Button Listener
        binding.summaryButton.setOnClickListener{
            val explicitIntent = Intent(applicationContext, SummaryActivity::class.java)
            startActivity(explicitIntent)
        }
    }
}
