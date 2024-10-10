package com.kk3k.workouttracker.Activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.ExerciseAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.viewmodel.SeriesViewModel
import kotlinx.coroutines.launch

class WorkoutDetailsActivity : AppCompatActivity() {

    // ViewModel for accessing series data (sets of exercises in a workout)
    private val seriesViewModel: SeriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        // Retrieve the workout ID passed from the previous activity
        val workoutId = intent.getIntExtra("WORKOUT_ID", -1)

        // Initialize RecyclerView to display exercises/sets
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercises)

        // Pass an empty list initially
        val adapter = ExerciseAdapter(emptyList())

        // Set the adapter and layout manager for the RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Collect data from the ViewModel and update the adapter with the exercise sets
        lifecycleScope.launch {
            seriesViewModel.getSetsForWorkout(workoutId).collect { sets ->
                adapter.submitList(sets) // Update adapter with the new data
            }
        }
    }
}
