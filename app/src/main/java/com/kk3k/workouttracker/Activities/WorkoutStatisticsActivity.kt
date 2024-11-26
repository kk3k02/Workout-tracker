package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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
        supportActionBar?.hide() // Hide the ActionBar

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
            findViewById<TextView>(R.id.tvWorkoutCount).text = "Liczba wykonanych treningów: $workoutCount"
            findViewById<TextView>(R.id.tvTotalDuration).text = "Łączny czas trwania treningów: ${formatDuration(totalDuration)}"
            findViewById<TextView>(R.id.tvTotalWeight).text = "Łączne użyte obciążenie: ${formatWeight(totalWeight)}"
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

    // Show a dialog for selecting a muscle group
    private fun showMuscleGroupSelectionDialog() {
        // Create a list of muscle group names from the TargetMuscle enum
        val muscleGroups = TargetMuscle.entries.map { it.name }

        // Create an ArrayAdapter with the custom layout
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


    // Show exercise selection dialog based on selected muscle group
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                // Map exercise names for display
                val exerciseNames = exerciseList.map { it.name }

                // Create an ArrayAdapter with the custom layout
                val adapter = ArrayAdapter(this@WorkoutStatisticsActivity, R.layout.dialog_list_item, exerciseNames)

                // Create the dialog
                val builder = AlertDialog.Builder(this@WorkoutStatisticsActivity, R.style.SelectExerciseDialogStyle)
                builder.setTitle("WYBIERZ ĆWICZENIE") // "SELECT EXERCISE"

                // Attach the adapter to the dialog
                builder.setAdapter(adapter) { _, which ->
                    // Handle selection
                    val selectedExercise = exerciseList[which]
                    selectedExerciseTextView.text = "WYBRANE ĆWICZENIE:\n\n ${selectedExercise.name}"
                    selectedExerciseId = selectedExercise.uid

                    // Fetch the exercise progression chart for the selected exercise
                    selectedExerciseId?.let { exerciseId ->
                        fetchExerciseProgressionChart(exerciseId)
                    }
                }

                // Add a "Cancel" button
                builder.setNegativeButton("COFNIJ", null) // "Cancel"

                // Show the dialog
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
            val seriesList = seriesDao.getSeriesForExercise(exerciseId)

            if (seriesList.isEmpty()) {
                Log.d("Chart", "Brak dostępnych informacji na temat tego ćwiczenia.")
                noDataMessage.visibility = View.VISIBLE
                chartContainer.visibility = View.GONE
                return@launch
            } else {
                noDataMessage.visibility = View.GONE
            }

            val datesMap = mutableMapOf<Long, Float>()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            for (series in seriesList) {
                val seriesWeight = series.repetitions * (series.weight ?: 0f)
                val workout = workoutDao.getWorkoutById(series.workoutId)
                val workoutDate = workout?.date ?: 0L

                if (workoutDate > 0) {
                    datesMap[workoutDate] = datesMap.getOrDefault(workoutDate, 0f) + seriesWeight
                }
            }

            val sortedDates = datesMap.keys.sorted()
            val entries = mutableListOf<Entry>()
            val formattedDates = mutableListOf<String>()

            var firstValue = 0f

            sortedDates.forEachIndexed { index, date ->
                val totalWeightForDate = datesMap[date] ?: 0f
                entries.add(Entry(index.toFloat(), totalWeightForDate))
                val formattedDate = dateFormat.format(Date(date))
                formattedDates.add(formattedDate)

                if (index == 0) {
                    firstValue = totalWeightForDate
                }
            }

            val lineDataSet = LineDataSet(entries, "Łączna waga użytego obciążenia")
            val lineData = LineData(lineDataSet)

            val lineChart = LineChart(this@WorkoutStatisticsActivity)
            lineChart.data = lineData
            lineChart.invalidate()

            val legend = lineChart.legend
            legend.textColor = getColor(R.color.white)

            val xAxis = lineChart.xAxis
            xAxis.textColor = getColor(R.color.white)
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return formattedDates.getOrElse(index) { "" }
                }
            }
            xAxis.granularity = 1f
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

            val yAxis = lineChart.axisLeft
            yAxis.textColor = getColor(R.color.white)
            yAxis.axisMinimum = firstValue
            yAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "$value kg"
                }
            }
            lineChart.axisRight.isEnabled = false

            // Add the description label
            val description = lineChart.description
            description.text = "Obciążenie ćwiczenia w czasie"
            description.textSize = 12f // Set the text size for better visibility
            description.textColor = getColor(R.color.white) // Set the color for the description
            description.isEnabled = true

            chartContainer.removeAllViews()
            chartContainer.addView(lineChart)
        }
    }
}