package com.kk3k.workouttracker.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.History_WorkoutAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.ViewModels.WorkoutViewModel
import kotlinx.coroutines.launch

class WorkoutHistoryActivity : AppCompatActivity() {

    // ViewModel to handle the data operations for workouts
    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        // Initialize RecyclerView and set up the adapter for the workout list
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkouts)
        val adapter = History_WorkoutAdapter(
            // Define the behavior when a workout item is clicked
            onWorkoutClick = { workoutId ->
                // Open the WorkoutHistoryDetailsActivity and pass the selected workout ID
                val intent = Intent(this, WorkoutHistoryDetailsActivity::class.java)
                intent.putExtra("WORKOUT_ID", workoutId)
                startActivity(intent)
            },
            // Define the behavior when the delete button is clicked
            onDeleteClick = { workout ->
                // Delete the selected workout
                deleteWorkout(workout)
            }
        )

        // Set the adapter and layout manager for the RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Collect finished workouts from the ViewModel and update the adapter with the data
        lifecycleScope.launch {
            workoutViewModel.finishedWorkouts.collect { workouts ->
                adapter.submitList(workouts)  // Update the RecyclerView with the list of finished workouts
            }
        }
    }

    // Function to delete a workout from the database
    private fun deleteWorkout(workout: Workout) {
        lifecycleScope.launch {
            workoutViewModel.deleteWorkout(workout)  // Remove the workout from the database
        }
    }
}