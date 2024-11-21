package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.TargetMuscle
import com.kk3k.workouttracker.db.dao.SeriesDao
import com.kk3k.workouttracker.db.dao.WorkoutDao
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutStatisticsActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var buttonSelectExercise: Button  // Button to select an exercise
    private lateinit var chartContainer: FrameLayout  // FrameLayout to display chart
    private lateinit var noDataMessage: TextView  // TextView to show when there is no data
    private lateinit var workoutDao: WorkoutDao  // DAO for workout data
    private lateinit var seriesDao: SeriesDao  // DAO for series data

    // Declare viewModel to interact with exercise data
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Declare TextView to display the selected exercise
    private lateinit var selectedExerciseTextView: TextView

    // Selected exercise and its ID (we need it for querying the series data)
    private var selectedExerciseId: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)

        // Initialize views
        buttonSelectExercise = findViewById(R.id.btnSelectExercise)
        chartContainer = findViewById(R.id.chartContainer)
        noDataMessage = findViewById(R.id.tvNoDataMessage)  // Add the TextView for the "No Data" message
        selectedExerciseTextView = findViewById(R.id.tvSelectedExercise)

        // Initialize workoutDao and seriesDao to access the database
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()  // Initialize SeriesDao

        // Initially hide the selected exercise TextView and the Select Exercise button
        selectedExerciseTextView.visibility = View.GONE
        buttonSelectExercise.visibility = View.GONE
        noDataMessage.visibility = View.GONE  // Initially hide the "No Data" message

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
            // Fetch all series for the selected exercise
            val seriesList = seriesDao.getSeriesForExercise(exerciseId)

            // If no series are found, log and show "No data" message
            if (seriesList.isEmpty()) {
                Log.d("Chart", "No series data found for this exercise")
                noDataMessage.visibility = View.VISIBLE  // Show "No data" message
                chartContainer.visibility = View.GONE  // Hide the chart container
                return@launch
            } else {
                noDataMessage.visibility = View.GONE  // Hide the "No data" message
            }

            // Map for storing total weight per workout date
            val datesMap = mutableMapOf<Long, Float>()

            // Format for displaying dates as dd/MM/yyyy
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            for (series in seriesList) {
                // Calculate the weight for each series (repetitions * weight)
                val seriesWeight = series.repetitions * (series.weight ?: 0f)

                // Get the workout date using the workoutDao
                val workout = workoutDao.getWorkoutById(series.workoutId)
                val workoutDate = workout?.date ?: 0L  // If no date, set to 0L (unknown date)

                // Check if the date is valid (not 0L) and update the total weight for the given workout date
                if (workoutDate > 0) {
                    datesMap[workoutDate] = datesMap.getOrDefault(workoutDate, 0f) + seriesWeight
                }
            }

            // Sort the workout dates (X axis) and generate the chart
            val sortedDates = datesMap.keys.sorted()  // Sorting dates in ascending order
            val entries = mutableListOf<Entry>()  // List to store chart points
            val formattedDates = mutableListOf<String>()  // List to store formatted dates for X axis

            var firstValue = 0f  // Variable to store the first value for Y axis minimum

            sortedDates.forEachIndexed { index, date ->
                val totalWeightForDate = datesMap[date] ?: 0f
                // Add a point to the chart (index as X axis, totalWeightForDate as Y axis)
                entries.add(Entry(index.toFloat(), totalWeightForDate))

                // Format the date as dd/MM/yyyy and add it to the formattedDates list
                val formattedDate = dateFormat.format(Date(date))
                formattedDates.add(formattedDate)

                // Store the first value for Y axis minimum setting
                if (index == 0) {
                    firstValue = totalWeightForDate
                }
            }

            // Create a dataset for the chart
            val lineDataSet = LineDataSet(entries, "Total Weight")
            val lineData = LineData(lineDataSet)

            // Create and update the chart
            val lineChart = LineChart(this@WorkoutStatisticsActivity)
            lineChart.data = lineData
            lineChart.invalidate()  // Refresh the chart

            // Set X and Y axis labels and configurations
            val xAxis = lineChart.xAxis
            // Set the X axis to display formatted dates
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return formattedDates.getOrElse(index) { "" }
                }
            }
            xAxis.granularity = 1f  // Ensures labels are displayed only once per date
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM  // Position labels at the bottom of X axis

            // Set the minimum value for Y axis (first value from the series)
            val yAxis = lineChart.axisLeft
            yAxis.axisMinimum = firstValue  // Set the first value as the minimum for Y axis
            yAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "$value kg"  // Append "kg" to the Y axis labels
                }
            }

            // Disable the right Y axis (usually not needed)
            lineChart.axisRight.isEnabled = false

            // Add the chart to the container
            chartContainer.removeAllViews()
            chartContainer.addView(lineChart)
        }
    }
}