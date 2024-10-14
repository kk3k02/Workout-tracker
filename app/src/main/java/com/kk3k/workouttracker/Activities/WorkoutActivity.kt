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
import com.kk3k.workouttracker.db.TargetMuscle
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
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>() // Mapping exercises to their series

    // Adapter for the RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    // Storing the workout ID from the database
    private var workoutId: Int? = null
    private lateinit var buttonSaveWorkout: Button // Button for "Save Workout"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Setting up the RecyclerView with the exercise adapter
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) },
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) },
            onDeleteSeries = { series -> removeSeries(series) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Initially disable the "Save Workout" button
        buttonSaveWorkout.isEnabled = false

        // Handling the "Add Exercise" button click
        buttonAddExercise.setOnClickListener {
            showMuscleGroupSelectionDialog() // Show muscle group selection
        }

        // Handling the "Save" button click
        buttonSaveWorkout.setOnClickListener {
            saveWorkout() // Save the workout when clicked
        }

        // Create a new workout in the database when the activity starts
        createNewWorkout()
    }

    // Creating a new workout object in the database
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

    // Display a dialog to select a muscle group
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup) // Show exercises filtered by selected muscle group
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Display a dialog with exercises filtered by the selected muscle group
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        // Fetch exercises for the selected muscle group from the database
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                // Display a dialog with the list of exercises
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()

                val builder = androidx.appcompat.app.AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    selectedExercises.add(selectedExercise)
                    exerciseAdapter.notifyDataSetChanged()

                    // Enable the "Save Workout" button if there are exercises
                    updateSaveButtonState()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Function to remove an exercise from the workout
    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid) // Remove the series related to the exercise
        exerciseAdapter.notifyDataSetChanged()

        // Update the state of the "Save Workout" button
        updateSaveButtonState()
    }

    // Function to remove a series from an exercise
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())

        // Remove the series from the database
        workoutViewModel.deleteSeries(series)
    }

    // Function to show a dialog to add a series to an exercise
    private fun showAddSeriesDialog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<android.widget.EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<android.widget.EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Add") { _, _ ->
            val reps = editTextReps.text.toString().toIntOrNull() ?: 0
            val weight = editTextWeight.text.toString().toFloatOrNull()

            // Add a new series to the exercise and update the list
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
        // Add any additional actions related to saving the workout here

        // Finish the activity
        finish()
    }

    // Function to manage the state of the "Save Workout" button
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()
    }
}
