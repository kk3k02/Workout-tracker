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

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private val selectedExercises = mutableListOf<Exercise>()
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>()
    private lateinit var exerciseAdapter: ExerciseAdapter

    private var workoutId: Int? = null
    private lateinit var buttonSaveWorkout: Button
    private var startTime: Long = 0L
    private var endTime: Long = 0L
    private var workoutNote: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize UI components
        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        buttonSaveWorkout = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Set up RecyclerView with its adapter and layout manager
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

        // Event listeners for UI interactions
        buttonAddExercise.setOnClickListener {
            showMuscleGroupSelectionDialog()
        }
        buttonSaveWorkout.setOnClickListener {
            showAddNoteDialog()
        }

        loadOrCreateWorkout()
    }

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

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun loadWorkoutData(workout: Workout) {
        workoutId = workout.uid
        val exercises = workoutViewModel.getExercisesForWorkout(workoutId!!)
        selectedExercises.addAll(exercises)

        exercises.forEach { exercise ->
            val seriesList = workoutViewModel.getSeriesForExercise(workoutId!!, exercise.uid)
            seriesMap[exercise.uid] = seriesList.toMutableList()
        }

        exerciseAdapter.notifyDataSetChanged()
        updateSaveButtonState()
    }

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

    private fun showAddCustomExerciseDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_cutstom_exercise, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextExerciseName)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextExerciseDescription)

        builder.setPositiveButton("Add") { dialog, _ ->
            val name = editTextName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please provide both the name and description of the exercise.", Toast.LENGTH_SHORT).show()
            } else {
                val newExercise = Exercise(
                    name = name,
                    targetMuscle = TargetMuscle.OTHER.name,
                    description = description,
                    image = null // Removed image logic
                )
                exerciseViewModel.insertExercise(newExercise)
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add a Note")

        val view = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)

        builder.setView(view)
        builder.setPositiveButton("OK") { _, _ ->
            workoutNote = editTextNote.text.toString()
            saveWorkout()
            showWorkoutSummary()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun saveWorkout() {
        endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        workoutId?.let { id ->
            workoutViewModel.setWorkoutDetails(id, duration, workoutNote)
        } ?: Log.e("WorkoutActivity", "Cannot save workout because workoutId is null.")
    }

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
                finish()
            }
            show()
        }
    }

    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid)
        exerciseAdapter.notifyDataSetChanged()
        updateSaveButtonState()
    }

    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())
        workoutId?.let {
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
                val newSeries = Series(
                    workoutId = id,
                    exerciseId = exercise.uid,
                    repetitions = reps,
                    weight = weight
                )
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
            Glide.with(this).load(exercise.image).into(imageView)
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
