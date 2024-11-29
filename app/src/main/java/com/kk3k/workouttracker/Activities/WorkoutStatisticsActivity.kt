package com.kk3k.workouttracker.Activities

// Import necessary packages
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

    // UI components for the activity
    private lateinit var buttonSelectExercise: Button  // Button to open muscle group selection dialog
    private lateinit var chartContainer: FrameLayout  // Container for displaying charts
    private lateinit var noDataMessage: TextView  // Message displayed when no data is available
    private lateinit var workoutDao: WorkoutDao  // DAO for accessing workout data
    private lateinit var seriesDao: SeriesDao  // DAO for accessing series data

    // ViewModel for managing exercise data
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // TextView to display the name of the selected exercise
    private lateinit var selectedExerciseTextView: TextView

    // Stores the ID of the currently selected exercise
    private var selectedExerciseId: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_statistics)
        supportActionBar?.hide() // Hide the ActionBar for a cleaner UI

        // Initialize UI components
        buttonSelectExercise = findViewById(R.id.btnSelectExercise)
        chartContainer = findViewById(R.id.chartContainer)
        noDataMessage = findViewById(R.id.tvNoDataMessage)  // TextView for "No Data" message
        selectedExerciseTextView = findViewById(R.id.tvSelectedExercise)

        // Access the database and initialize DAOs
        val workoutDatabase = AppDatabase.getDatabase(this)
        workoutDao = workoutDatabase.workoutDao()
        seriesDao = workoutDatabase.seriesDao()

        // Initially hide components until they are needed
        selectedExerciseTextView.visibility = View.GONE
        buttonSelectExercise.visibility = View.GONE
        noDataMessage.visibility = View.GONE

        // Set up click listener for the "Select Exercise" button
        buttonSelectExercise.setOnClickListener {
            showMuscleGroupSelectionDialog() // Opens muscle group selection dialog
        }

        // Set up click listeners for the "General" and "Chart" buttons
        findViewById<Button>(R.id.btnGeneral).setOnClickListener {
            showGeneralContent()  // Display general workout statistics
            showGeneralStatsView()  // Show the general stats layout
            selectedExerciseTextView.visibility = View.GONE  // Hide exercise info when showing general stats
            buttonSelectExercise.visibility = View.GONE  // Hide "Select Exercise" button in general view
        }

        findViewById<Button>(R.id.btnChart).setOnClickListener {
            showChartView()  // Display the chart layout
            selectedExerciseTextView.visibility = View.VISIBLE  // Show selected exercise info
            buttonSelectExercise.visibility = View.VISIBLE  // Show "Select Exercise" button
        }

        // Initially show general workout statistics by default
        showGeneralContent()
        showGeneralStatsView()
    }

    // Display general statistics such as total workouts, duration, and weight
    @SuppressLint("SetTextI18n")
    private fun showGeneralContent() {
        lifecycleScope.launch {
            // Fetch statistics from the database
            val workoutCount = workoutDao.getFinishedWorkoutCount() // Number of completed workouts
            val totalDuration = workoutDao.getTotalWorkoutDuration() ?: 0L // Total workout duration
            val totalWeight = seriesDao.getTotalWeightUsed() ?: 0f // Total weight lifted

            // Update the UI with the fetched statistics
            findViewById<TextView>(R.id.tvWorkoutCount).text = "Liczba wykonanych treningów: $workoutCount"
            findViewById<TextView>(R.id.tvTotalDuration).text = "Łączny czas trwania treningów: ${formatDuration(totalDuration)}"
            findViewById<TextView>(R.id.tvTotalWeight).text = "Łączne użyte obciążenie: ${formatWeight(totalWeight)}"
        }
    }

    // Format duration in milliseconds into a human-readable string (HH:mm:ss)
    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationInMillis: Long): String {
        val hours = (durationInMillis / 3600000) % 24
        val minutes = (durationInMillis / 60000) % 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Format weight into a readable string with two decimal places
    @SuppressLint("DefaultLocale")
    private fun formatWeight(weight: Float): String {
        return String.format("%.2f kg", weight)
    }

    // Display a dialog for selecting a muscle group
    private fun showMuscleGroupSelectionDialog() {
        // Retrieve the names of all muscle groups
        val muscleGroups = TargetMuscle.entries.map { it.name }

        // Create an ArrayAdapter for displaying muscle groups in a list
        val adapter = ArrayAdapter(this, R.layout.dialog_list_item, muscleGroups)

        // Build and display the selection dialog
        val builder = AlertDialog.Builder(this, R.style.SelectExerciseDialogStyle)
        builder.setTitle("Wybierz partię mięśniową") // Set dialog title
        builder.setAdapter(adapter) { _, which ->
            val selectedMuscleGroup = TargetMuscle.entries[which] // Get selected muscle group
            showExerciseSelectionDialog(selectedMuscleGroup) // Show exercises for the selected group
        }
        builder.setNegativeButton("COFNIJ", null) // Add cancel button
        builder.show() // Show the dialog
    }

    // Display a dialog for selecting an exercise within the chosen muscle group
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showExerciseSelectionDialog(muscleGroup: TargetMuscle) {
        val exerciseList = mutableListOf<Exercise>()

        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                // Filter exercises based on the selected muscle group
                exerciseList.clear()
                exerciseList.addAll(exercises.filter { it.targetMuscle == muscleGroup.name })

                // Map exercise names for displaying in the dialog
                val exerciseNames = exerciseList.map { it.name }

                // Create an adapter for the exercise list
                val adapter = ArrayAdapter(this@WorkoutStatisticsActivity, R.layout.dialog_list_item, exerciseNames)

                // Build and display the exercise selection dialog
                val builder = AlertDialog.Builder(this@WorkoutStatisticsActivity, R.style.SelectExerciseDialogStyle)
                builder.setTitle("WYBIERZ ĆWICZENIE") // Dialog title
                builder.setAdapter(adapter) { _, which ->
                    val selectedExercise = exerciseList[which] // Get the selected exercise
                    selectedExerciseTextView.text = "WYBRANE ĆWICZENIE:\n\n ${selectedExercise.name}" // Update UI
                    selectedExerciseId = selectedExercise.uid // Save the ID of the selected exercise

                    // Fetch and display progression data for the selected exercise
                    selectedExerciseId?.let { exerciseId ->
                        fetchExerciseProgressionChart(exerciseId)
                    }
                }
                builder.setNegativeButton("COFNIJ", null) // Add cancel button
                builder.show() // Show the dialog
            }
        }
    }

    // Display the chart layout and hide general statistics
    private fun showChartView() {
        findViewById<LinearLayout>(R.id.generalStatsContainer).visibility = View.GONE // Hide general stats
        chartContainer.visibility = View.VISIBLE // Show chart container
    }

    // Display general statistics and hide the chart layout
    private fun showGeneralStatsView() {
        findViewById<LinearLayout>(R.id.generalStatsContainer).visibility = View.VISIBLE // Show general stats
        chartContainer.visibility = View.GONE // Hide chart container
    }

    // Fetch and display a progression chart for the selected exercise
    private fun fetchExerciseProgressionChart(exerciseId: Int) {
        lifecycleScope.launch {
            val seriesList = seriesDao.getSeriesForExercise(exerciseId) // Fetch series data for the exercise

            if (seriesList.isEmpty()) {
                Log.d("Chart", "Brak dostępnych informacji na temat tego ćwiczenia.") // Log no data available
                noDataMessage.visibility = View.VISIBLE // Show "No Data" message
                chartContainer.visibility = View.GONE // Hide chart container
                return@launch
            } else {
                noDataMessage.visibility = View.GONE // Hide "No Data" message
            }

            // Map workout dates to total weights lifted
            val datesMap = mutableMapOf<Long, Float>()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            for (series in seriesList) {
                val seriesWeight = series.repetitions * (series.weight ?: 0f) // Calculate total weight for the series
                val workout = workoutDao.getWorkoutById(series.workoutId) // Fetch workout data
                val workoutDate = workout?.date ?: 0L

                if (workoutDate > 0) {
                    datesMap[workoutDate] = datesMap.getOrDefault(workoutDate, 0f) + seriesWeight
                }
            }

            // Prepare data for the chart
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
                    firstValue = totalWeightForDate // Save the first value for reference
                }
            }

            // Create and configure the chart
            val lineDataSet = LineDataSet(entries, "Łączna waga użytego obciążenia") // Dataset for the chart
            val lineData = LineData(lineDataSet)

            val lineChart = LineChart(this@WorkoutStatisticsActivity)
            lineChart.data = lineData
            lineChart.invalidate() // Refresh the chart with new data

            // Configure chart appearance
            val legend = lineChart.legend
            legend.textColor = getColor(R.color.white) // Set legend text color

            val xAxis = lineChart.xAxis
            xAxis.textColor = getColor(R.color.white) // Set x-axis text color
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return formattedDates.getOrElse(index) { "" } // Format x-axis labels as dates
                }
            }
            xAxis.granularity = 1f
            xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

            val yAxis = lineChart.axisLeft
            yAxis.textColor = getColor(R.color.white) // Set y-axis text color
            yAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "$value kg" // Format y-axis labels as weights
                }
            }
            lineChart.axisRight.isEnabled = false // Disable right y-axis

            // Configure chart description
            val description = lineChart.description
            description.text = "Obciążenie ćwiczenia w czasie" // Description text
            description.textSize = 12f // Set description text size
            description.textColor = getColor(R.color.white) // Set description text color
            description.isEnabled = true

            // Display the chart in the container
            chartContainer.removeAllViews()
            chartContainer.addView(lineChart)
        }
    }
}
