package com.kk3k.workouttracker.Activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
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
    // ViewModel instance to access body measurement data
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_statistics)

        // Initialize chart container and buttons
        val chartContainer: FrameLayout = findViewById(R.id.chartContainer)
        val buttonWeightChart: Button = findViewById(R.id.buttonWeightChart)
        val buttonDimensions: Button = findViewById(R.id.buttonDimensions)

        // Create LineCharts for weight and body measurements
        val weightChart = LineChart(this)
        val bodyMeasurementsChart = LineChart(this)

        // Initially, show nothing in the container
        chartContainer.removeAllViews()

        // Button click listeners for toggling between charts
        buttonWeightChart.setOnClickListener {
            chartContainer.removeAllViews()
            chartContainer.addView(weightChart)
            setupWeightChart(weightChart) // Configure and display the weight chart
        }

        buttonDimensions.setOnClickListener {
            chartContainer.removeAllViews()
            chartContainer.addView(bodyMeasurementsChart)
            setupBodyMeasurementsChart(bodyMeasurementsChart) // Configure and display the body measurements chart
        }
    }

    // Configure the weight chart to display weight progression over time
    private fun setupWeightChart(chart: LineChart) {
        lifecycleScope.launch {
            // Collect weight data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    // Map the weight measurements to chart entries
                    val entries = measurements.map { measurement ->
                        measurement.date?.let { Entry(it.toFloat(), measurement.weight?.toFloat() ?: 0f) }
                    }

                    // Create dataset for weight data
                    val dataSet = LineDataSet(entries, "Weight Over Time").apply {
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        setDrawCircles(true) // Enable dots at data points
                        setCircleColor(Color.RED) // Dot color
                        circleRadius = 4f // Dot size
                        setDrawValues(false) // Disable value labels
                    }

                    val lineData = LineData(dataSet)

                    // Configure chart appearance and behavior
                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Weight Progression" }

                        xAxis.apply {
                            textColor = Color.BLACK
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true) // Show vertical grid lines
                            gridColor = Color.LTGRAY
                            valueFormatter = DateAxisValueFormatter() // Format X-axis values as dates
                        }

                        axisLeft.apply {
                            textColor = Color.BLACK
                            setDrawGridLines(true) // Show horizontal grid lines
                            gridColor = Color.LTGRAY
                        }

                        axisRight.apply {
                            isEnabled = true
                            setDrawGridLines(true) // Enable optional right axis grid lines
                            gridColor = Color.LTGRAY
                        }

                        setNoDataText("No weight data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    // Clear chart if no data is available
                    chart.clear()
                    chart.setNoDataText("No weight data available.")
                }
            }
        }
    }

    // Configure the body measurements chart with multiple datasets
    private fun setupBodyMeasurementsChart(chart: LineChart) {
        lifecycleScope.launch {
            // Collect body measurements data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    val dataSets = mutableListOf<LineDataSet>()

                    // Create datasets for each body measurement type
                    val bicepsEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.biceps?.toFloat() ?: 0f) } }
                    val tricepsEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.triceps?.toFloat() ?: 0f) } }
                    val chestEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.chest?.toFloat() ?: 0f) } }
                    val waistEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.waist?.toFloat() ?: 0f) } }
                    val hipsEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.hips?.toFloat() ?: 0f) } }
                    val thighsEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.thighs?.toFloat() ?: 0f) } }
                    val calvesEntries = measurements.map { it.date?.let { it1 -> Entry(it1.toFloat(), it.calves?.toFloat() ?: 0f) } }

                    // Add datasets with appropriate colors and labels
                    dataSets.add(createDataSet(bicepsEntries, "Biceps", Color.RED))
                    dataSets.add(createDataSet(tricepsEntries, "Triceps", Color.GREEN))
                    dataSets.add(createDataSet(chestEntries, "Chest", Color.BLUE))
                    dataSets.add(createDataSet(waistEntries, "Waist", Color.YELLOW))
                    dataSets.add(createDataSet(hipsEntries, "Hips", Color.MAGENTA))
                    dataSets.add(createDataSet(thighsEntries, "Thighs", Color.CYAN))
                    dataSets.add(createDataSet(calvesEntries, "Calves", Color.GRAY))

                    val lineData = LineData(dataSets as List<ILineDataSet>)

                    // Configure chart appearance and behavior
                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Body Measurements Progression" }

                        xAxis.apply {
                            textColor = Color.BLACK
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true) // Show vertical grid lines
                            gridColor = Color.LTGRAY
                            valueFormatter = DateAxisValueFormatter() // Format X-axis values as dates
                        }

                        axisLeft.apply {
                            textColor = Color.BLACK
                            setDrawGridLines(true) // Show horizontal grid lines
                            gridColor = Color.LTGRAY
                        }

                        axisRight.apply {
                            isEnabled = true
                            setDrawGridLines(true) // Enable optional right axis grid lines
                            gridColor = Color.LTGRAY
                        }

                        legend.apply {
                            isEnabled = true // Show legend
                            textColor = Color.BLACK
                            form = Legend.LegendForm.LINE
                        }

                        setNoDataText("No measurement data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    // Clear chart if no data is available
                    chart.clear()
                    chart.setNoDataText("No measurement data available.")
                }
            }
        }
    }

    // Helper function to create a dataset for a specific measurement
    private fun createDataSet(entries: List<Entry?>, label: String, color: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = color
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawCircles(true) // Enable dots at data points
            setCircleColor(color) // Match dot color to line color
            circleRadius = 4f // Dot size
            setDrawValues(false) // Disable value labels
        }
    }

    // Custom value formatter to display dates on the X-axis
    private class DateAxisValueFormatter : ValueFormatter() {
        private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            return sdf.format(value.toLong())
        }
    }
}
