package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class BodyStatisticsActivity : AppCompatActivity() {
    // ViewModel to manage and access body measurement data
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    // UI components for buttons and chart container
    private lateinit var buttonGeneral: Button
    private lateinit var buttonWeightChart: Button
    private lateinit var buttonDimensions: Button
    private lateinit var chartContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_statistics)

        // Initialize the views
        chartContainer = findViewById(R.id.chartContainer)
        buttonGeneral = findViewById(R.id.buttonGeneral)
        buttonWeightChart = findViewById(R.id.buttonWeightChart)
        buttonDimensions = findViewById(R.id.buttonDimensions)

        // Create LineCharts for weight and body measurements
        val weightChart = LineChart(this)
        val bodyMeasurementsChart = LineChart(this)

        // Set "General" button as the default active button
        deactivateButtons()
        buttonGeneral.isEnabled = false

        // Initially, show nothing in the chart container
        chartContainer.removeAllViews()

        // Set click listeners for the buttons
        buttonGeneral.setOnClickListener {
            deactivateButtons()
            buttonGeneral.isEnabled = false
            chartContainer.removeAllViews() // Clear the chart container
            showGeneralContent() // Show the general content (e.g. text, image, etc.)
        }

        buttonWeightChart.setOnClickListener {
            deactivateButtons()
            buttonWeightChart.isEnabled = false
            chartContainer.removeAllViews()
            chartContainer.addView(weightChart) // Add weight chart to container
            setupWeightChart(weightChart) // Configure and display the weight chart
        }

        buttonDimensions.setOnClickListener {
            deactivateButtons()
            buttonDimensions.isEnabled = false
            chartContainer.removeAllViews()
            chartContainer.addView(bodyMeasurementsChart) // Add measurements chart to container
            setupBodyMeasurementsChart(bodyMeasurementsChart) // Configure and display the measurements chart
        }

        // Automatically show general content (information about biceps size change)
        showGeneralContent()  // Call this method to display biceps size change info when the activity starts
    }

    // Deactivate all buttons (make them clickable again)
    private fun deactivateButtons() {
        buttonGeneral.isEnabled = true
        buttonWeightChart.isEnabled = true
        buttonDimensions.isEnabled = true
    }

    // Show general content when the "General" button is clicked
    @SuppressLint("SetTextI18n")
    private fun showGeneralContent() {
        lifecycleScope.launch {
            // Collect body measurements data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    // Find the first (oldest) and latest (most recent) measurement for biceps
                    val firstBicepsMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestBicepsMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }

                    if (firstBicepsMeasurement != null && latestBicepsMeasurement != null) {
                        val firstBiceps = firstBicepsMeasurement.biceps?.toFloat() ?: 0f
                        val latestBiceps = latestBicepsMeasurement.biceps?.toFloat() ?: 0f

                        // Calculate the difference in biceps size
                        val changeInBiceps = latestBiceps - firstBiceps
                        val changeText = if (changeInBiceps >= 0) {
                            "+${changeInBiceps.toInt()} cm"
                        } else {
                            "${changeInBiceps.toInt()} cm"
                        }

                        // Display the result in the chart container
                        val generalTextView = TextView(this@BodyStatisticsActivity).apply {
                            text = "Change in Biceps: $changeText"
                            textSize = 18f
                            setTextColor(Color.BLACK)
                            gravity = Gravity.CENTER
                        }

                        chartContainer.addView(generalTextView)
                    }
                } else {
                    // If no measurements are available
                    val generalTextView = TextView(this@BodyStatisticsActivity).apply {
                        text = "No measurements available."
                        textSize = 18f
                        setTextColor(Color.BLACK)
                        gravity = Gravity.CENTER
                    }

                    chartContainer.addView(generalTextView)
                }
            }
        }
    }

    // Configure the weight chart to display weight progression over time
    private fun setupWeightChart(chart: LineChart) {
        lifecycleScope.launch {
            // Collect weight data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    // Determine the first date and minimum weight for scaling
                    val firstDate = measurements.minOfOrNull { it.date ?: Long.MAX_VALUE }?.toFloat() ?: 0f
                    val minWeight = measurements.mapNotNull { it.weight?.toFloat() }.minOrNull() ?: 0f

                    // Map measurements to chart entries
                    val entries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { Entry(it.toFloat(), measurement.weight?.toFloat() ?: 0f) }
                    }

                    // Create dataset for the chart
                    val dataSet = LineDataSet(entries, "Weight Over Time").apply {
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        setDrawCircles(true) // Show data points as circles
                        setCircleColor(Color.RED) // Circle color
                        circleRadius = 4f // Circle size
                        setDrawValues(false) // Disable value labels
                    }

                    val lineData = LineData(dataSet)

                    // Configure chart appearance
                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Weight Progression" }

                        xAxis.apply {
                            textColor = Color.BLACK
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true) // Enable vertical grid lines
                            gridColor = Color.LTGRAY
                            valueFormatter = DateAxisValueFormatter() // Format X-axis values as dates
                            axisMinimum = firstDate // Start X-axis from the first date
                            textSize = 12f
                        }

                        axisLeft.apply {
                            textColor = Color.BLACK
                            setDrawGridLines(true) // Enable horizontal grid lines
                            gridColor = Color.LTGRAY
                            axisMinimum = minWeight // Start Y-axis from the minimum weight
                            textSize = 12f
                            labelCount = 6
                            valueFormatter = YAxisValueFormatter("kg") // Add "kg" to labels
                        }

                        axisRight.isEnabled = false

                        legend.apply {
                            textColor = Color.BLACK
                            form = Legend.LegendForm.LINE
                        }

                        setNoDataText("No weight data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    chart.clear() // Clear the chart if no data is available
                    chart.setNoDataText("No weight data available.")
                }
            }
        }
    }

    // Configure the body measurements chart to display multiple datasets
    private fun setupBodyMeasurementsChart(chart: LineChart) {
        lifecycleScope.launch {
            // Collect body measurements data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    // Determine the minimum measurement value for scaling
                    val minMeasurement = measurements.flatMap {
                        listOfNotNull(it.biceps, it.triceps, it.chest, it.waist, it.hips, it.thighs, it.calves)
                    }.minOrNull()?.toFloat() ?: 0f

                    val dataSets = mutableListOf<LineDataSet>()

                    // Create datasets for different body parts
                    val bicepsEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.biceps?.toFloat() ?: 0f) } }
                    val tricepsEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.triceps?.toFloat() ?: 0f) } }
                    val chestEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.chest?.toFloat() ?: 0f) } }
                    val waistEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.waist?.toFloat() ?: 0f) } }
                    val hipsEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.hips?.toFloat() ?: 0f) } }
                    val thighsEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.thighs?.toFloat() ?: 0f) } }
                    val calvesEntries = measurements.mapNotNull { it.date?.let { date -> Entry(date.toFloat(), it.calves?.toFloat() ?: 0f) } }

                    // Add datasets for the chart
                    dataSets.add(createDataSet(bicepsEntries, "Biceps", Color.RED))
                    dataSets.add(createDataSet(tricepsEntries, "Triceps", Color.GREEN))
                    dataSets.add(createDataSet(chestEntries, "Chest", Color.BLUE))
                    dataSets.add(createDataSet(waistEntries, "Waist", Color.YELLOW))
                    dataSets.add(createDataSet(hipsEntries, "Hips", Color.MAGENTA))
                    dataSets.add(createDataSet(thighsEntries, "Thighs", Color.CYAN))
                    dataSets.add(createDataSet(calvesEntries, "Calves", Color.GRAY))

                    val lineData = LineData(dataSets as List<ILineDataSet>)

                    // Configure chart appearance
                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Body Measurements Progression" }

                        xAxis.apply {
                            textColor = Color.BLACK
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            valueFormatter = DateAxisValueFormatter() // Format X-axis values as dates
                            textSize = 12f
                        }

                        axisLeft.apply {
                            textColor = Color.BLACK
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            axisMinimum = minMeasurement // Start Y-axis from the minimum measurement
                            textSize = 12f
                            labelCount = 6
                            valueFormatter = YAxisValueFormatter("cm") // Add "cm" to labels
                        }

                        axisRight.isEnabled = false

                        legend.apply {
                            isEnabled = true
                            textColor = Color.BLACK
                            form = Legend.LegendForm.LINE
                        }

                        setNoDataText("No measurement data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    chart.clear() // Clear the chart if no data is available
                    chart.setNoDataText("No measurement data available.")
                }
            }
        }
    }

    // Helper function to create a dataset for a specific body part
    private fun createDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = color
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawCircles(true) // Show data points as circles
            setCircleColor(color)
            circleRadius = 4f
            setDrawValues(false)
        }
    }

    // Custom value formatter for X-axis to display dates
    private class DateAxisValueFormatter : ValueFormatter() {
        private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            return sdf.format(value.toLong())
        }
    }

    // Custom value formatter for Y-axis to display units
    private class YAxisValueFormatter(private val unit: String) : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "$value $unit"
        }
    }
}
