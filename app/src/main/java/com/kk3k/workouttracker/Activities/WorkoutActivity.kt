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
import android.app.AlertDialog
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView

class WorkoutActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // List of selected exercises for the workout
    private val selectedExercises = mutableListOf<Exercise>()
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>() // Mapping exercises to series

    // Adapter for RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    // Store the workout ID in the database
    private var workoutId: Int? = null
    private lateinit var buttonSaveWorkout: Button // "Save Workout" button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Set up RecyclerView
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) },
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) },
            onDeleteSeries = { series -> removeSeries(series) },
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) } // New callback for showing exercise info
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Initially disable the "Save Workout" button
        buttonSaveWorkout.isEnabled = false

        // Handle click on the "Add Exercise" button
        buttonAddExercise.setOnClickListener {
            showMuscleGroupSelectionDialog()
        }

        // Handle click on the "Save Workout" button
        buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }

        // Create a new workout in the database when the activity starts
        createNewWorkout()
    }

    // Function to create a new workout object in the database
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = "Workout in progress"
            )

            // Save workout and set its ID
            workoutViewModel.insertWorkout(workout)
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
            }
        }
    }

    // Function to display a dialog with a list of muscle groups
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Function to display a dialog with a list of exercises for the selected muscle group
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        // Fetch exercises from the database for the selected muscle group
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                // Show a dialog with the list of exercises
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()

                val builder = androidx.appcompat.app.AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")

                // Add the "Add custom exercise" option for the "Other" group
                if (muscleGroup == TargetMuscle.OTHER) {
                    val options = exerciseNames.toMutableList()
                    options.add("Add custom exercise") // Option to add a new exercise
                    builder.setItems(options.toTypedArray()) { _, which ->
                        if (which == exerciseNames.size) {
                            // "Add custom exercise" was clicked
                            showAddCustomExerciseDialog()
                        } else {
                            // An existing exercise was clicked
                            val selectedExercise = exerciseList[which]
                            selectedExercises.add(selectedExercise)
                            exerciseAdapter.notifyDataSetChanged()

                            // Enable the "Save Workout" button if there are exercises
                            updateSaveButtonState()
                        }
                    }
                } else {
                    builder.setItems(exerciseNames) { _, which ->
                        val selectedExercise = exerciseList[which]
                        selectedExercises.add(selectedExercise)
                        exerciseAdapter.notifyDataSetChanged()

                        // Enable the "Save Workout" button if there are exercises
                        updateSaveButtonState()
                    }
                }

                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Function to display a dialog for adding a new custom exercise
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showAddCustomExerciseDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_cutstom_exercise, null)
        builder.setView(view)

        val editTextName = view.findViewById<android.widget.EditText>(R.id.editTextExerciseName)
        val editTextDescription = view.findViewById<android.widget.EditText>(R.id.editTextExerciseDescription)

        builder.setPositiveButton("Add") { dialog, _ ->
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (name.isNotEmpty()) {
                val newExercise = Exercise(
                    name = name,
                    targetMuscle = TargetMuscle.OTHER.name, // Automatically set to "Other"
                    description = description
                )

                // Save the new exercise in the database
                exerciseViewModel.insertExercise(newExercise)

                // Close the dialog after adding the new exercise
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Function to remove an exercise
    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid) // Also remove associated series
        exerciseAdapter.notifyDataSetChanged()

        // Update the state of the "Save Workout" button
        updateSaveButtonState()
    }

    // Function to remove a series
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())

        // Remove the series from the database
        workoutViewModel.deleteSeries(series)
    }

    // Function to display a dialog for adding a series
    private fun showAddSeriesDialog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<android.widget.EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<android.widget.EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Add") { _, _ ->
            val reps = editTextReps.text.toString().toIntOrNull() ?: 0
            val weight = editTextWeight.text.toString().toFloatOrNull()

            // Add the new series to the map and refresh the list
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

            // Save the series in the database
            workoutViewModel.insertSeries(newSeries)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Function to display exercise info dialog with animated GIF support
    @SuppressLint("MissingInflatedId")
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        // Set the exercise name and description in the dialog
        textViewName.text = exercise.name
        textViewDescription.text = exercise.description

        // Check if the exercise has an image (ByteArray representing GIF)
        if (exercise.image != null && exercise.image!!.isNotEmpty()) {
            // Load the GIF using Glide
            Glide.with(this)
                .asGif()
                .load(exercise.image) // Load ByteArray as GIF
                .into(imageView)
        } else {
            // Fallback: use a default icon if there is no image
            imageView.setImageResource(android.R.drawable.ic_dialog_info)
        }

        builder.setView(view)
        builder.setPositiveButton("Close", null)
        builder.show()
    }


    // Function to save the workout and mark it as finished
    private fun saveWorkout() {
        // Check if workoutId is not null, then mark the workout as finished
        workoutId?.let { id ->
            workoutViewModel.markWorkoutAsFinished(id)  // Mark the workout as finished in the database
        }

        // Close the activity after saving and marking the workout as finished
        finish()
    }


    // Function to manage the state of the "Save Workout" button
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()
    }
}
