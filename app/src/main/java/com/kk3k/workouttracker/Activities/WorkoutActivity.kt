package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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
        // Create a list of muscle group names from the TargetMuscle enum
        val muscleGroups = TargetMuscle.entries.map { it.name }

        // Create an ArrayAdapter with a custom layout for each item
        // The layout R.layout.dialog_list_item defines how each list item will appear
        val adapter = ArrayAdapter(this, R.layout.dialog_list_item, muscleGroups)

        // Create an AlertDialog.Builder with a custom dialog style
        val builder = AlertDialog.Builder(this, R.style.SelectExerciseDialogStyle)

        // Set the dialog title
        builder.setTitle("Wybierz partię mięśniową") // "Select Muscle Group"

        // Attach the adapter to the dialog and handle item selection
        builder.setAdapter(adapter) { _, which ->
            // Get the selected muscle group based on the clicked position
            val selectedMuscleGroup = TargetMuscle.entries[which]

            // Open a new dialog or screen to display exercises for the selected muscle group
            showExerciseSelectionDialog(selectedMuscleGroup)
        }

        // Add a "Cancel" button to the dialog
        builder.setNegativeButton("COFNIJ", null) // "Cancel"

        // Display the dialog
        builder.show()
    }

    // Show a dialog to select an exercise from the chosen muscle group
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        // A mutable list to hold exercises for the selected muscle group
        val exerciseList = mutableListOf<Exercise>()

        // Launch a coroutine to collect exercises from the ViewModel
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                // Clear and update the exercise list with exercises matching the selected muscle group
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                // Map exercise names for display in the dialog
                val exerciseNames = exerciseList.map { it.name }

                // Add "Add Custom Exercise" option for the "OTHER" muscle group
                val displayNames = if (muscleGroup == TargetMuscle.OTHER) {
                    exerciseNames + "Dodaj własne ćwiczenie" // "Add Custom Exercise"
                } else {
                    exerciseNames
                }

                // Create an ArrayAdapter with the custom layout
                val adapter = ArrayAdapter(
                    this@WorkoutActivity,
                    R.layout.dialog_list_item, // Custom layout for list items
                    displayNames
                )

                // Create an AlertDialog.Builder with the custom dialog style
                val builder = AlertDialog.Builder(this@WorkoutActivity, R.style.SelectExerciseDialogStyle)
                builder.setTitle("Wybierz ćwiczenie") // "Select Exercise"

                // Set the adapter to the dialog
                builder.setAdapter(adapter) { _, which ->
                    if (muscleGroup == TargetMuscle.OTHER && which == exerciseNames.size) {
                        // Show dialog to add a custom exercise
                        showAddCustomExerciseDialog()
                    } else {
                        // Add the selected exercise to the list
                        val selectedExercise = exerciseList[which]
                        selectedExercises.add(selectedExercise)
                        exerciseAdapter.notifyDataSetChanged() // Notify the adapter about data changes
                        updateSaveButtonState() // Update the save button state
                    }
                }

                // Add a "Cancel" button to the dialog
                builder.setNegativeButton("COFNIJ", null) // "Cancel"

                // Display the dialog
                builder.show()
            }
        }
    }

    // Show a dialog to add a custom exercise
    private fun showAddCustomExerciseDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_add_cutstom_exercise, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextExerciseName)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextExerciseDescription)

        builder.setPositiveButton("DODAJ") { dialog, _ ->
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            // Validate the input
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "PROSZĘ PODAĆ NAZWĘ I OPIS ĆWICZENIA", Toast.LENGTH_SHORT).show()
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
        builder.setNegativeButton("COFNIJ", null)
        builder.show()
    }

    // Show a dialog to add a note to the workout
    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)

        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->
            workoutNote = editTextNote.text.toString()  // Get the note from the user
            saveWorkout()  // Save the workout with the note
            showWorkoutSummary()  // Show a summary of the workout
        }
        builder.setNegativeButton("COFNIJ", null)
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
    @SuppressLint("DefaultLocale")
    private fun showWorkoutSummary() {
        // Calculate the duration in seconds
        val durationSeconds = (endTime - startTime) / 1000
        // Convert the duration to minutes and seconds
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds) // Format as MM:SS

        val musclesInvolved = selectedExercises.map { it.targetMuscle }.toSet().joinToString(", ")
        val allWeights = seriesMap.values.flatten().mapNotNull { it.weight }
        val minWeight = allWeights.minOrNull() ?: 0f
        val maxWeight = allWeights.maxOrNull() ?: 0f
        val totalWeight = allWeights.sum()

        // Summary text
        val summaryText = """
        Czas trwania: $formattedDuration
        
        Liczba wykonanych ćwiczeń: ${selectedExercises.size}
        
        Zaangażowane mięśnie: $musclesInvolved
        
        Min. Obciążenie: $minWeight kg
        
        Max. Obciążenie: $maxWeight kg
        
        Łączna waga obciążenia: $totalWeight kg
    """.trimIndent()

        // Create and show the dialog with the custom style
        AlertDialog.Builder(this, R.style.SummaryDialogTheme).apply {
            setTitle("Podsumowanie treningu") // Dialog title
            setMessage(summaryText) // Summary text
            setPositiveButton("ZAMKNIJ") { dialog, _ ->
                dialog.dismiss() // Close the dialog
                finish() // Finish the activity after showing the summary
            }
            show() // Show the dialog
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
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)

        builder.setPositiveButton("DODAJ") { _, _ ->
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
        builder.setNegativeButton("COFNIJ", null)
        builder.show()
    }

    // Show a dialog with information about an exercise
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
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
        builder.setPositiveButton("ZAMKNIJ", null)
        builder.show()
    }

    // Update the state of the "Save Workout" button based on the selected exercises
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty()  // Enable the button if there are selected exercises
    }
}