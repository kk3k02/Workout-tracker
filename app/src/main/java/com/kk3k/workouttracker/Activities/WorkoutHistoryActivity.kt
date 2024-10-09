package com.kk3k.workouttracker.Activities

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

    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewWorkoutHistory)
        val adapter = WorkoutAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Zamiast observe, użyj lifecycleScope i collect, aby zbierać dane z Flow
        lifecycleScope.launch {
            workoutViewModel.allWorkouts.collect { workouts ->
                adapter.submitList(workouts)
            }
        }
    }
}
