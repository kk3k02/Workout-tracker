package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.ExerciseAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Lista dodanych ćwiczeń do treningu
    private val selectedExercises = mutableListOf<Exercise>()

    // Adapter do RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Ustawienia RecyclerView
        exerciseAdapter = ExerciseAdapter(selectedExercises)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Obsługa kliknięcia na przycisk Add Exercise
        buttonAddExercise.setOnClickListener {
            showExerciseSelectionDialog()
        }

        // Tworzenie nowego treningu w bazie danych
        workoutViewModel.insertWorkout(
            Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = "Workout in progress"
            )
        )
    }

    // Funkcja do wyświetlenia dialogu z listą ćwiczeń
    private fun showExerciseSelectionDialog() {
        val exerciseList = mutableListOf<Exercise>()

        // Pobieranie ćwiczeń z bazy danych
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises)

                // Wyświetlenie dialogu z listą ćwiczeń
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()

                val builder = androidx.appcompat.app.AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    selectedExercises.add(selectedExercise)
                    exerciseAdapter.notifyDataSetChanged()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }
}
