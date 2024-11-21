package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.dao.SeriesDao
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.TargetMuscle
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class WorkoutStatisticsActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var buttonSelectExercise: Button  // Button to select an exercise
    private lateinit var chartContainer: FrameLayout  // FrameLayout to display chart
    private lateinit var workoutDao: WorkoutDao  // DAO for workout data
    private lateinit var seriesDao: SeriesDao  // DAO for series data

    // Declare viewModel to interact with exercise data
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Declare TextView to display the selected exercise
    private lateinit var selectedExerciseTextView: TextView

    // Selected exercise and its ID (we need it for querying the series data)
    private var selectedExerciseId: Int? = null
    private var workoutId: Int? = null  // Store workout ID, replace with actual logic to get workoutId

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonSelectExercise = findViewById(R.id.btnSelectExercise)
        chartContainer = findViewById(R.id.chartContainer)
        selectedExerciseTextView = findViewById(R.id.tvSelectedExercise)

        // Initialize workoutDao and seriesDao to access the database
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()  // Initialize SeriesDao

        // Initially hide the selected exercise TextView and the Select Exercise button
        selectedExerciseTextView.visibility = View.GONE
        buttonSelectExercise.visibility = View.GONE

        // Set click listener for the "Select Exercise" button
        buttonSelectExercise.setOnClickListener {
            showMuscleGroupSelectionDialog()  // When clicked, show dialog to select muscle group
        }

        // Set click listeners for "General" and "Chart" buttons
        findViewById<Button>(R.id.btnGeneral).setOnClickListener {
            showGeneralContent()  // Show general statistics
            showGeneralStatsView()  // Show the general statistics view
            selectedExerciseTextView.visibility = View.GONE  // Hide selected exercise info when General is clicked
            buttonSelectExercise.visibility = View.GONE  // Hide the Select Exercise button
        }

        findViewById<Button>(R.id.btnChart).setOnClickListener {
            showChartView()  // Show chart view
            selectedExerciseTextView.visibility = View.VISIBLE  // Show selected exercise info when Chart is clicked
            buttonSelectExercise.visibility = View.VISIBLE  // Show Select Exercise button when Chart is clicked
        }

        // Initially show the general content
        showGeneralContent()
        showGeneralStatsView()
    }

    // Show general content in the container (e.g., total workouts, duration, weight)
    @SuppressLint("SetTextI18n")
    private fun showGeneralContent() {
        lifecycleScope.launch {
            // Get the count of finished workouts from the database
            val workoutCount = workoutDao.getFinishedWorkoutCount()
            // Get the total duration of all finished workouts
            val totalDuration = workoutDao.getTotalWorkoutDuration() ?: 0L
            // Get the total weight used in all series (weight * repetitions)
            val totalWeight = seriesDao.getTotalWeightUsed() ?: 0f

            // Update UI with fetched statistics
            findViewById<TextView>(R.id.tvWorkoutCount).text = "Number of completed workouts: $workoutCount"
            findViewById<TextView>(R.id.tvTotalDuration).text = "Total duration of all workouts: ${formatDuration(totalDuration)}"
            findViewById<TextView>(R.id.tvTotalWeight).text = "Total weight used: ${formatWeight(totalWeight)}"
        }
    }

    // Format duration in milliseconds into a human-readable format (HH:mm:ss)
    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationInMillis: Long): String {
        val hours = (durationInMillis / 3600000) % 24
        val minutes = (durationInMillis / 60000) % 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Format weight into a human-readable format (e.g., kg)
    @SuppressLint("DefaultLocale")
    private fun formatWeight(weight: Float): String {
        return String.format("%.2f kg", weight)
    }

    // Show muscle group selection dialog
    private fun showMuscleGroupSelectionDialog() {
        val muscleGroups = TargetMuscle.entries.map { it.name }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Muscle Group")
        builder.setItems(muscleGroups.toTypedArray()) { _, which ->
            // When a muscle group is selected, show the exercise selection dialog
            val selectedMuscleGroup = TargetMuscle.entries[which]
            showExerciseSelectionDialog(selectedMuscleGroup)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Show exercise selection dialog based on selected muscle group
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                val exerciseNames = exerciseList.map { it.name }.toTypedArray()
                val builder = AlertDialog.Builder(this@WorkoutStatisticsActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    selectedExerciseTextView.text = "Selected Exercise: ${selectedExercise.name}"
                    selectedExerciseId = selectedExercise.uid

                    // Fetch the exercise progression chart for the selected exercise
                    selectedExerciseId?.let { exerciseId ->
                        fetchExerciseProgressionChart(exerciseId)
                    }
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Show chart view and hide general stats
    private fun showChartView() {
        findViewById<LinearLayout>(R.id.generalStatsContainer).visibility = View.GONE
        chartContainer.visibility = View.VISIBLE
    }

    // Show general stats and hide chart
    private fun showGeneralStatsView() {
        findViewById<LinearLayout>(R.id.generalStatsContainer).visibility = View.VISIBLE
        chartContainer.visibility = View.GONE
    }

    // Fetch exercise progression chart based on selected exercise and workout
    private fun fetchExerciseProgressionChart(exerciseId: Int) {
        lifecycleScope.launch {
            workoutId = 1  // Replace with actual logic to get workoutId
            workoutId?.let { id ->
                val seriesList = seriesDao.getSeriesForWorkoutAndExercise(id, exerciseId)

                if (seriesList.isEmpty()) {
                    Log.d("Chart", "No series data found for this exercise")
                }

                // Map the series data into chart entries (time on X-axis, weight on Y-axis)
                val entries = seriesList.mapIndexed { index, series ->
                    Entry(index.toFloat(), series.weight ?: 0f)
                }

                val lineDataSet = LineDataSet(entries, "Weight Progression")
                val lineData = LineData(lineDataSet)

                // Create a LineChart, set its data, and refresh it
                val lineChart = LineChart(this@WorkoutStatisticsActivity)
                lineChart.data = lineData
                lineChart.invalidate()

                // Add the chart to the container and remove any previous views
                chartContainer.removeAllViews()
                chartContainer.addView(lineChart)
            }
        }
    }
}
