package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.WorkoutAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class WorkoutHistoryActivity : AppCompatActivity() {

    // ViewModel to handle the data operations for workouts
    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        // Initialize RecyclerView and set up adapter for workout list
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkouts)
        val adapter = WorkoutAdapter { workoutId ->
            // Handle click on a workout item - open WorkoutDetailsActivity
            val intent = Intent(this, WorkoutDetailsActivity::class.java)
            intent.putExtra("WORKOUT_ID", workoutId)
            startActivity(intent)
        }

        // Set the adapter and layout manager for the RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Collect data from ViewModel and assign it to the adapter
        lifecycleScope.launch {
            workoutViewModel.allWorkouts.collect { workouts ->
                adapter.submitList(workouts)
            }
        }
    }
}
