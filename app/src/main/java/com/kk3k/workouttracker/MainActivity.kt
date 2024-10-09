package com.kk3k.workouttracker

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pobierz i wyświetl dane z bazy w konsoli
        lifecycleScope.launch {
            //workoutViewModel.deleteAllWorkouts()
            workoutViewModel.allWorkouts.collect { workouts ->
                if (workouts.isEmpty()) {
                    Log.d("WorkoutLog", "No workouts found in the database")
                } else {
                    workouts.forEach { workout ->
                        Log.d("WorkoutLog", "Workout: ${workout.uid}, ${workout.notes}")
                    }
                }
            }
        }
    }
}
