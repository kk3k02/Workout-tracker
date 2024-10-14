package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
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
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // List of exercises added to the workout
    private val selectedExercises = mutableListOf<Exercise>()
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>() // Mapping exercises to series

    // Adapter for RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    // Storing workout ID in the database
    private var workoutId: Int? = null
    private lateinit var buttonSaveWorkout: Button // "Save Workout" button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // RecyclerView setup
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) },
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) },
            onDeleteSeries = { series -> removeSeries(series) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Initially set the "Save Workout" button to inactive
        buttonSaveWorkout.isEnabled = false

        // Handle Add Exercise button click
        buttonAddExercise.setOnClickListener {
            showExerciseSelectionDialog()
        }

        // Handle Save button click
        buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }

        // Create a new workout in the database when the activity starts
        createNewWorkout()
    }

    // Create a new workout entry in the database
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = "Workout in progress"
            )

            // Insert workout and set its ID
            workoutViewModel.insertWorkout(workout)
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
            }
        }
    }

    // Show a dialog with a list of exercises to select from
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog() {
        val exerciseList = mutableListOf<Exercise>()

        // Fetch exercises from the database
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises)

                // Display the list of exercises in a dialog
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()

                val builder = androidx.appcompat.app.AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    selectedExercises.add(selectedExercise)
                    exerciseAdapter.notifyDataSetChanged()

                    // Enable "Save Workout" button if there are exercises added
                    updateSaveButtonState()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Function to remove an exercise
    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid) // Remove series related to the exercise
        exerciseAdapter.notifyDataSetChanged()

        // Update the state of the Save button
        updateSaveButtonState()
    }

    // Function to remove a series
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())

        // Delete the series from the database
        workoutViewModel.deleteSeries(series)
    }

    // Show a dialog to add a new series for a specific exercise
    private fun showAddSeriesDialog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<android.widget.EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<android.widget.EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Add") { _, _ ->
            val reps = editTextReps.text.toString().toIntOrNull() ?: 0
            val weight = editTextWeight.text.toString().toFloatOrNull()

            // Add the new series to the map and update the list
            val newSeries = Series(
                workoutId = workoutId ?: 0,  // Use the current workout ID
                exerciseId = exercise.uid,
                repetitions = reps,
                weight = weight
            )

            seriesMap[exercise.uid]?.add(newSeries) ?: run {
                seriesMap[exercise.uid] = mutableListOf(newSeries)
            }
            exerciseAdapter.updateSeries(exercise.uid, seriesMap[exercise.uid] ?: mutableListOf())

            // Insert the new series into the database
            workoutViewModel.insertSeries(newSeries)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Function to save the workout
    private fun saveWorkout() {
        // Additional actions related to saving the workout can be added here

        // Finish the activity
        finish()
    }

    // Function to manage the state of the "Save Workout" button
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()
    }
}
