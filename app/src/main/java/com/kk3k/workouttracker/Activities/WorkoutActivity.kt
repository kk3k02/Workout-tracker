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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

class WorkoutActivity : AppCompatActivity() {

    // ViewModels for accessing and modifying workout and exercise data
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Selected exercises and their corresponding series
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
        supportActionBar?.hide() // Hide the ActionBar for a cleaner UI

        // Initialize UI components
        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Configure the RecyclerView with an adapter and layout manager
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) }, // Callback for removing exercises
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) }, // Callback for adding series
            onDeleteSeries = { series -> removeSeries(series) },         // Callback for removing series
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) } // Callback for showing exercise details
        )
        recyclerView.layoutManager = LinearLayoutManager(this) // Linear layout for listing exercises
        recyclerView.adapter = exerciseAdapter

        // Initially disable the save button; it will be enabled later when exercises are added
        buttonSaveWorkout.isEnabled = false

        // Track the start time of the workout
        startTime = System.currentTimeMillis()

        // Set up click listeners for buttons
        buttonAddExercise.setOnClickListener {
            showMuscleGroupSelectionDialog() // Show dialog for selecting a muscle group
        }
        buttonSaveWorkout.setOnClickListener {
            showAddNoteDialog() // Show dialog for adding notes to the workout
        }

        // Load an existing workout or create a new one
        loadOrCreateWorkout()
    }

    // Load an unfinished workout if it exists, or create a new workout
    private fun loadOrCreateWorkout() {
        lifecycleScope.launch {
            val unfinishedWorkout = workoutViewModel.getUnfinishedWorkout() // Check for any unfinished workout
            if (unfinishedWorkout != null) {
                workoutId = unfinishedWorkout.uid
                loadWorkoutData(unfinishedWorkout) // Load data for the unfinished workout
            } else {
                createNewWorkout() // Create a new workout if none exists
            }
        }
    }

    // Load data (exercises and series) associated with an existing workout
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadWorkoutData(workout: Workout) {
        workoutId = workout.uid // Set the workout ID
        val exercises = workoutViewModel.getExercisesForWorkout(workoutId!!)
        selectedExercises.addAll(exercises) // Add the workout's exercises to the list

        // Load and map series for each exercise
        exercises.forEach { exercise ->
            val seriesList = workoutViewModel.getSeriesForExercise(workoutId!!, exercise.uid)
            seriesMap[exercise.uid] = seriesList.toMutableList()
        }

        // Refresh the adapter and update the save button state
        exerciseAdapter.notifyDataSetChanged()
        updateSaveButtonState()
    }

    // Create a new workout if no unfinished workout exists
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = workoutNote,
                isFinished = false
            )

            workoutViewModel.insertWorkout(workout) // Insert the workout into the database
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
                if (workoutId == null) {
                    Log.e("WorkoutActivity", "Failed to create a new workout.")
                }
            }
        }
    }

    // Show a dialog to select a muscle group
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.getDisplayName() } // List of muscle group names
        val adapter = ArrayAdapter(this, R.layout.dialog_list_item, muscleGroups)

        val builder = AlertDialog.Builder(this, R.style.SelectExerciseDialogStyle)
        builder.setTitle("Wybierz partię mięśniową") // Title for the dialog

        builder.setAdapter(adapter) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which] // Get the selected muscle group
            showExerciseSelectionDialog(selectedMuscleGroup) // Show exercises for the selected group
        }

        builder.setNegativeButton("COFNIJ", null) // Cancel button
        builder.show()
    }

    // Show a dialog to select an exercise from the chosen muscle group
    @SuppressLint("NotifyDataSetChanged")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                // Filter exercises for the selected muscle group
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                val exerciseNames = exerciseList.map { it.name } // Names for display
                val displayNames = if (muscleGroup == TargetMuscle.OTHER) {
                    exerciseNames + "Dodaj własne ćwiczenie" // Add custom exercise option
                } else {
                    exerciseNames
                }

                val adapter = ArrayAdapter(this@WorkoutActivity, R.layout.dialog_list_item, displayNames)
                val builder = AlertDialog.Builder(this@WorkoutActivity, R.style.SelectExerciseDialogStyle)
                builder.setTitle("Wybierz ćwiczenie")

                builder.setAdapter(adapter) { _, which ->
                    if (muscleGroup == TargetMuscle.OTHER && which == exerciseNames.size) {
                        showAddCustomExerciseDialog() // Show dialog to add custom exercise
                    } else {
                        val selectedExercise = exerciseList[which]
                        selectedExercises.add(selectedExercise) // Add the selected exercise
                        exerciseAdapter.notifyDataSetChanged() // Refresh the UI
                        updateSaveButtonState()
                    }
                }

                builder.setNegativeButton("COFNIJ", null) // Cancel button
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

            // Ensure valid input
            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "PROSZĘ PODAĆ NAZWĘ I OPIS ĆWICZENIA", Toast.LENGTH_SHORT).show()
            } else {
                val newExercise = Exercise(
                    name = name,
                    targetMuscle = TargetMuscle.OTHER.name,
                    description = description,
                    image = null // No image provided for custom exercises
                )
                exerciseViewModel.insertExercise(newExercise) // Insert the new exercise into the database
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("COFNIJ", null) // Cancel button
        builder.show()
    }

    // Show a dialog to add a note to the workout
    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)

        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->
            workoutNote = editTextNote.text.toString() // Save the workout note
            saveWorkout() // Save workout details
            showWorkoutSummary() // Display a summary of the workout
        }
        builder.setNegativeButton("COFNIJ", null) // Cancel button
        builder.show()
    }

    // Save workout details such as duration and note
    private fun saveWorkout() {
        endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        workoutId?.let { id ->
            workoutViewModel.setWorkoutDetails(id, duration, workoutNote) // Save to database
        } ?: Log.e("WorkoutActivity", "Cannot save workout because workoutId is null.")
    }

    // Display a summary of the workout session
    @SuppressLint("DefaultLocale")
    private fun showWorkoutSummary() {
        // Calculate workout duration in seconds
        val durationSeconds = (endTime - startTime) / 1000
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, seconds)

        // Generate summary details
        val musclesInvolved = selectedExercises.map {
            when (it.targetMuscle) {
                "BICEPS" -> "BICEPS"
                "TRICEPS" -> "TRICEPS"
                "SHOULDERS" -> "RAMIONA"
                "CHEST" -> "KLATKA PIERSIOWA"
                "BACK" -> "PLECY"
                "FOREARMS" -> "PRZEDRAMIONA"
                "ABS" -> "BRZUCH"
                "LEGS" -> "NOGI"
                "CALVES" -> "ŁYDKI"
                else -> "INNE"
            }
        }.toSet().joinToString(", ")

        val allWeights = seriesMap.values.flatten().mapNotNull { it.weight }
        val minWeight = allWeights.minOrNull() ?: 0f
        val maxWeight = allWeights.maxOrNull() ?: 0f
        val totalWeight = allWeights.sum()

        val summaryText = """
            Czas trwania: $formattedDuration
            
            Liczba wykonanych ćwiczeń: ${selectedExercises.size}
            
            Zaangażowane mięśnie: $musclesInvolved
            
            Min. Obciążenie: $minWeight kg
            
            Max. Obciążenie: $maxWeight kg
            
            Łączna waga obciążenia: $totalWeight kg
        """.trimIndent()

        // Display the summary in a dialog
        AlertDialog.Builder(this, R.style.SummaryDialogTheme).apply {
            setTitle("Podsumowanie treningu")
            setMessage(summaryText)
            setPositiveButton("ZAMKNIJ") { dialog, _ ->
                dialog.dismiss() // Close the dialog
                finish() // End the activity
            }
            show()
        }
    }

    // Remove an exercise from the list of selected exercises
    @SuppressLint("NotifyDataSetChanged")
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid)
        exerciseAdapter.notifyDataSetChanged() // Refresh the UI
        updateSaveButtonState()
    }

    // Remove a series from the workout
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())
        workoutId?.let {
            workoutViewModel.deleteSeries(series) // Remove from database
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
                workoutViewModel.insertSeries(newSeries) // Insert into the database
            } ?: Log.e("WorkoutActivity", "Cannot add series because workoutId is null.")
        }
        builder.setNegativeButton("COFNIJ", null) // Cancel button
        builder.show()
    }

    // Show details of a specific exercise in a dialog
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        textViewName.text = exercise.name // Display the exercise name
        textViewDescription.text = exercise.description // Display the exercise description

        if (exercise.image != null && exercise.image!!.isNotEmpty()) {
            Glide.with(this).load(exercise.image).into(imageView) // Load exercise image if available
        } else {
            imageView.setImageResource(android.R.drawable.ic_dialog_info) // Default image
        }
        builder.setView(view)
        builder.setPositiveButton("ZAMKNIJ", null) // Close button
        builder.show()
    }

    // Update the state of the "Save Workout" button
    private fun updateSaveButtonState() {
        buttonSaveWorkout.isEnabled = selectedExercises.isNotEmpty() // Enable if there are exercises
    }

    // Add pre-defined sample workouts and exercises for testing purposes
    private fun addSampleWorkouts() {
        lifecycleScope.launch {
            // Get all exercises from the database
            val allExercises = exerciseViewModel.allExercises.firstOrNull() ?: emptyList()

            // Ensure there are enough exercises to create sample workouts
            if (allExercises.size < 4) {
                Log.e("WorkoutActivity", "Not enough exercises in the database.")
                return@launch
            }

            // Select the first 4 exercises for the sample workouts
            val exercisesToUse = allExercises.take(4)

            // Define sample workouts
            val workouts = listOf(
                Workout(uid = 1, date = System.currentTimeMillis(), duration = 3600000L, notes = "Workout 1: Chest and Back", isFinished = true),
                Workout(uid = 2, date = System.currentTimeMillis() - 86400000L, duration = 4500000L, notes = "Workout 2: Legs", isFinished = true),
                Workout(uid = 3, date = System.currentTimeMillis() - 2 * 86400000L, duration = 4200000L, notes = "Workout 3: Arms", isFinished = true),
                Workout(uid = 4, date = System.currentTimeMillis() - 3 * 86400000L, duration = 3900000L, notes = "Workout 4: Cardio", isFinished = true),
                Workout(uid = 5, date = System.currentTimeMillis() - 4 * 86400000L, duration = 3600000L, notes = "Workout 5: Full Body", isFinished = true),
                Workout(uid = 6, date = System.currentTimeMillis() - 5 * 86400000L, duration = 4200000L, notes = "Workout 6: Mobility and Stability", isFinished = true),
                Workout(uid = 7, date = System.currentTimeMillis() - 6 * 86400000L, duration = 3300000L, notes = "Workout 7: Back and Biceps", isFinished = true),
                Workout(uid = 8, date = System.currentTimeMillis() - 7 * 86400000L, duration = 4500000L, notes = "Workout 8: Max Strength", isFinished = true),
                Workout(uid = 9, date = System.currentTimeMillis() - 8 * 86400000L, duration = 3000000L, notes = "Workout 9: Chest and Triceps", isFinished = true),
                Workout(uid = 10, date = System.currentTimeMillis() - 9 * 86400000L, duration = 3600000L, notes = "Workout 10: Functional Training", isFinished = true)
            )

            // Add each workout and associate exercises with series
            workouts.forEach { workout ->
                workoutViewModel.insertWorkout(workout)

                exercisesToUse.forEach { exercise ->
                    val numberOfSeries = Random.nextInt(3, 6) // Random number of series (3-5)
                    repeat(numberOfSeries) {
                        val series = Series(
                            workoutId = workout.uid, // Use pre-defined workout ID
                            exerciseId = exercise.uid,
                            repetitions = Random.nextInt(5, 12), // Random repetitions (5-12)
                            weight = Random.nextInt(8, 25).toFloat() // Random weight (8-25 kg)
                        )
                        workoutViewModel.insertSeries(series) // Insert the series into the database
                    }
                }
            }
        }
    }
}
