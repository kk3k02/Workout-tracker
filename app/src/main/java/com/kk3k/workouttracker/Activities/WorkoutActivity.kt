package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kk3k.workouttracker.Adapters.ExerciseAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.ViewModels.WorkoutViewModel
import com.kk3k.workouttracker.db.TargetMuscle
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {

    // ViewModels to interact with workout and exercise data
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Lists and maps to store selected exercises and their series
    private val selectedExercises = mutableListOf<Exercise>()
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>()
    private lateinit var exerciseAdapter: ExerciseAdapter

    // Workout-related variables
    private var workoutId: Int? = null
    private lateinit var buttonSaveWorkout: Button
    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var workoutNote: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        supportActionBar?.hide() // Hide the ActionBar

        // Initialize UI components
        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Set up the RecyclerView with the adapter and layout manager
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) },
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) },
            onDeleteSeries = { series -> removeSeries(series) },
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Disable the save button initially; it will be enabled when there's data
        buttonSaveWorkout.isEnabled = false

        // Track the start time of the workout session
        startTime = System.currentTimeMillis()

        // Set event listeners for buttons
        buttonAddExercise.setOnClickListener {
            // Show the muscle group selection dialog when the "Add Exercise" button is clicked
            showMuscleGroupSelectionDialog()
        }
        buttonSaveWorkout.setOnClickListener {
            // Show the note input dialog when the "Save Workout" button is clicked
            showAddNoteDialog()
        }

        // Load or create a workout when the activity is created
        loadOrCreateWorkout()
    }

    // Load the existing unfinished workout or create a new one
    private fun loadOrCreateWorkout() {
        lifecycleScope.launch {
            val unfinishedWorkout = workoutViewModel.getUnfinishedWorkout()
            if (unfinishedWorkout != null) {
                workoutId = unfinishedWorkout.uid
                loadWorkoutData(unfinishedWorkout)  // Load data for the unfinished workout
            } else {
                createNewWorkout()  // Create a new workout if no unfinished one is found
            }
        }
    }

    // Load data for an existing workout, including exercises and series
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadWorkoutData(workout: Workout) {
        workoutId = workout.uid
        val exercises = workoutViewModel.getExercisesForWorkout(workoutId!!)
        selectedExercises.addAll(exercises)  // Add the exercises to the list

        exercises.forEach { exercise ->
            val seriesList = workoutViewModel.getSeriesForExercise(workoutId!!, exercise.uid)
            seriesMap[exercise.uid] = seriesList.toMutableList()  // Add the series for each exercise
        }

        exerciseAdapter.notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView
        updateSaveButtonState()  // Update the save button state based on selected exercises
    }

    // Create a new workout if no unfinished one exists
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = workoutNote,
                isFinished = false
            )

            workoutViewModel.insertWorkout(workout)  // Insert the new workout into the database
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
                if (workoutId == null) {
                    Log.e("WorkoutActivity", "Failed to create a new workout.")
                }
            }
        }
    }

    // Show a dialog for selecting a muscle group
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup)  // Show exercises for the selected muscle group
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Show a dialog to select an exercise from the chosen muscle group
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()
                val builder = AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")
                if (muscleGroup == TargetMuscle.OTHER) {
                    val options = exerciseNames.toMutableList()
                    options.add("Add custom exercise")  // Option to add a custom exercise
                    builder.setItems(options.toTypedArray()) { _, which ->
                        if (which == exerciseNames.size) {
                            showAddCustomExerciseDialog()  // Show dialog to add a custom exercise
                        } else {
                            val selectedExercise = exerciseList[which]
                            selectedExercises.add(selectedExercise)  // Add the selected exercise
                            exerciseAdapter.notifyDataSetChanged()
                            updateSaveButtonState()
                        }
                    }
                } else {
                    builder.setItems(exerciseNames) { _, which ->
                        val selectedExercise = exerciseList[which]
                        selectedExercises.add(selectedExercise)  // Add the selected exercise
                        exerciseAdapter.notifyDataSetChanged()
                        updateSaveButtonState()
                    }
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Show a dialog to add a custom exercise
    private fun showAddCustomExerciseDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_cutstom_exercise, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextExerciseName)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextExerciseDescription)

        builder.setPositiveButton("Add") { dialog, _ ->
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            // Validate the input
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please provide both the name and description of the exercise.", Toast.LENGTH_SHORT).show()
            } else {
                val newExercise = Exercise(
                    name = name,
                    targetMuscle = TargetMuscle.OTHER.name,
                    description = description,
                    image = null // No image logic in this case
                )
                exerciseViewModel.insertExercise(newExercise)  // Insert the custom exercise into the database
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Show a dialog to add a note to the workout
    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add a Note")

        val view = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)

        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->
            workoutNote = editTextNote.text.toString()  // Get the note from the user
            saveWorkout()  // Save the workout with the note
            showWorkoutSummary()  // Show a summary of the workout
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Save the workout data including duration and note
    private fun saveWorkout() {
        endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        workoutId?.let { id ->
            workoutViewModel.setWorkoutDetails(id, duration, workoutNote)  // Save workout details
        } ?: Log.e("WorkoutActivity", "Cannot save workout because workoutId is null.")
    }

    // Show a summary dialog with workout details
    private fun showWorkoutSummary() {
        val durationSeconds = (endTime - startTime) / 1000
        val musclesInvolved = selectedExercises.map { it.targetMuscle }.toSet().joinToString(", ")
        val allWeights = seriesMap.values.flatten().mapNotNull { it.weight }
        val minWeight = allWeights.minOrNull() ?: 0f
        val maxWeight = allWeights.maxOrNull() ?: 0f
        val totalWeight = allWeights.sum()

        val summaryText = """
            Workout Duration: $durationSeconds seconds
            Total Exercises: ${selectedExercises.size}
            Muscles Involved: $musclesInvolved
            Min Weight: $minWeight kg
            Max Weight: $maxWeight kg
            Total Weight: $totalWeight kg
        """.trimIndent()

        AlertDialog.Builder(this).apply {
            setTitle("Workout Summary")
            setMessage(summaryText)
            setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
                finish()  // Finish the activity after showing the summary
            }
            show()
        }
    }

    // Remove an exercise from the selected exercises list
    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid)
        exerciseAdapter.notifyDataSetChanged()  // Notify adapter of data change
        updateSaveButtonState()  // Update save button state
    }

    // Remove a series from the workout
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())
        workoutId?.let {
            workoutViewModel.deleteSeries(series)  // Delete the series from the database
        } ?: Log.e("WorkoutActivity", "Cannot delete series because workoutId is null.")
    }

    // Show a dialog to add a series to a specific exercise
    private fun showAddSeriesDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Add") { _, _ ->
            val reps = editTextReps.text.toString().toIntOrNull() ?: 0
            val weight = editTextWeight.text.toString().toFloatOrNull()
            workoutId?.let { id ->
                val newSeries = Series(
                    workoutId = id,
                    exerciseId = exercise.uid,
                    repetitions = reps,
                    weight = weight
                )
                seriesMap[exercise.uid]?.add(newSeries) ?: run { seriesMap[exercise.uid] = mutableListOf(newSeries) }
                exerciseAdapter.updateSeries(exercise.uid, seriesMap[exercise.uid] ?: mutableListOf())
                workoutViewModel.insertSeries(newSeries)  // Insert the new series into the database
            } ?: Log.e("WorkoutActivity", "Cannot add series because workoutId is null.")
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Show a dialog with information about an exercise
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        textViewName.text = exercise.name
        textViewDescription.text = exercise.description

        if (exercise.image != null && exercise.image!!.isNotEmpty()) {
            Glide.with(this).load(exercise.image).into(imageView)  // Load the exercise image if available
        } else {
            imageView.setImageResource(android.R.drawable.ic_dialog_info)  // Default image if none exists
        }
        builder.setView(view)
        builder.setPositiveButton("Close", null)
        builder.show()
    }

    // Update the state of the "Save Workout" button based on the selected exercises
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()  // Enable the button if there are selected exercises
    }
}