package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

    private val workoutViewModel: WorkoutViewModel by viewModels() // ViewModel for workouts
    private val exerciseViewModel: ExerciseViewModel by viewModels() // ViewModel for exercises

    private val selectedExercises = mutableListOf<Exercise>() // List of selected exercises
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>() // Map linking exercises to their series
    private lateinit var exerciseAdapter: ExerciseAdapter // Adapter for RecyclerView

    private var workoutId: Int? = null // ID of the workout in the database
    private lateinit var buttonSaveWorkout: Button // Button to save the workout
    private var selectedImageBytes: ByteArray? = null // Holds image data for exercises

    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var workoutNote: String = "" // Variable to store workout notes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize UI elements
        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Set up RecyclerView with ExerciseAdapter
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) }, // Callback to delete exercise
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) }, // Callback to add series
            onDeleteSeries = { series -> removeSeries(series) }, // Callback to delete series
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) } // Callback to show exercise info
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Disable save button initially
        buttonSaveWorkout.isEnabled = false

        // Record workout start time
        startTime = System.currentTimeMillis()

        // Handle click for adding exercises
        buttonAddExercise.setOnClickListener {
            showMuscleGroupSelectionDialog()
        }

        // Handle click for saving workout
        buttonSaveWorkout.setOnClickListener {
            showAddNoteDialog() // Show dialog to add a note before saving
        }

        // Load existing unfinished workout or create a new one
        loadOrCreateWorkout()
    }

    // Loads an existing unfinished workout or creates a new one
    private fun loadOrCreateWorkout() {
        lifecycleScope.launch {
            val unfinishedWorkout = workoutViewModel.getUnfinishedWorkout()

            if (unfinishedWorkout != null) {
                workoutId = unfinishedWorkout.uid
                loadWorkoutData(unfinishedWorkout)
            } else {
                createNewWorkout()
            }
        }
    }

    // Loads data for an existing workout
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadWorkoutData(workout: Workout) {
        workoutId = workout.uid

        // Load exercises and their series for the workout
        val exercises = workoutViewModel.getExercisesForWorkout(workoutId!!)
        selectedExercises.addAll(exercises)

        exercises.forEach { exercise ->
            val seriesList = workoutViewModel.getSeriesForExercise(workoutId!!, exercise.uid)
            seriesMap[exercise.uid] = seriesList.toMutableList()
        }

        exerciseAdapter.notifyDataSetChanged()
        updateSaveButtonState()
    }

    // Creates a new workout and inserts it into the database
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = workoutNote,
                isFinished = false
            )

            workoutViewModel.insertWorkout(workout)
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
                if (workoutId == null) {
                    Log.e("WorkoutActivity", "Failed to create a new workout.")
                }
            }
        }
    }

    // Shows a dialog to select a muscle group
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Shows exercises for a selected muscle group
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
                    options.add("Add custom exercise")
                    builder.setItems(options.toTypedArray()) { _, which ->
                        if (which == exerciseNames.size) {
                            showAddCustomExerciseDialog()
                        } else {
                            val selectedExercise = exerciseList[which]
                            selectedExercises.add(selectedExercise)
                            exerciseAdapter.notifyDataSetChanged()
                            updateSaveButtonState()
                        }
                    }
                } else {
                    builder.setItems(exerciseNames) { _, which ->
                        val selectedExercise = exerciseList[which]
                        selectedExercises.add(selectedExercise)
                        exerciseAdapter.notifyDataSetChanged()
                        updateSaveButtonState()
                    }
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Shows a dialog to add a custom exercise
    private fun showAddCustomExerciseDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_cutstom_exercise, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextExerciseName)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextExerciseDescription)
        val buttonSelectImage = view.findViewById<Button>(R.id.buttonSelectImage)

        buttonSelectImage.setOnClickListener { /* TODO: Implement image selection */ }

        builder.setPositiveButton("Add") { dialog, _ ->
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (name.isNotEmpty()) {
                val newExercise = Exercise(
                    name = name,
                    targetMuscle = TargetMuscle.OTHER.name,
                    description = description,
                    image = selectedImageBytes
                )
                exerciseViewModel.insertExercise(newExercise)
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Shows a dialog to add a note before saving the workout
    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add a Note")

        // Custom layout with an EditText for note input
        val view = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)

        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->
            workoutNote = editTextNote.text.toString() // Save the note
            saveWorkout() // Save workout with note
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Saves the workout and sets the end time
    private fun saveWorkout() {
        endTime = System.currentTimeMillis() // Set end time

        // Calculate duration as the difference between start and end times
        val duration = endTime - startTime

        workoutId?.let { id ->
            workoutViewModel.setWorkoutDuration(id, duration)
            workoutViewModel.setWorkoutNote(id, workoutNote) // Update note in database
            workoutViewModel.markWorkoutAsFinished(id)
        } ?: Log.e("WorkoutActivity", "Cannot save workout because workoutId is null.")

        finish() // Close the activity
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid)
        exerciseAdapter.notifyDataSetChanged()
        updateSaveButtonState()
    }

    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())
        workoutId?.let { _ ->
            workoutViewModel.deleteSeries(series)
        } ?: Log.e("WorkoutActivity", "Cannot delete series because workoutId is null.")
    }

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
                val newSeries = Series(workoutId = id, exerciseId = exercise.uid, repetitions = reps, weight = weight)
                seriesMap[exercise.uid]?.add(newSeries) ?: run { seriesMap[exercise.uid] = mutableListOf(newSeries) }
                exerciseAdapter.updateSeries(exercise.uid, seriesMap[exercise.uid] ?: mutableListOf())
                workoutViewModel.insertSeries(newSeries)
            } ?: Log.e("WorkoutActivity", "Cannot add series because workoutId is null.")
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        textViewName.text = exercise.name
        textViewDescription.text = exercise.description

        if (exercise.image != null && exercise.image!!.isNotEmpty()) {
            Glide.with(this).asGif().load(exercise.image).into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_dialog_info)
        }
        builder.setView(view)
        builder.setPositiveButton("Close", null)
        builder.show()
    }

    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()
    }
}
