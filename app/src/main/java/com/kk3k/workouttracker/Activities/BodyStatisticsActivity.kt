package com.kk3k.workouttracker.Activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class BodyStatisticsActivity : AppCompatActivity() {
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_statistics)

        val chartContainer: FrameLayout = findViewById(R.id.chartContainer)
        val buttonWeightChart: Button = findViewById(R.id.buttonWeightChart)
        val buttonDimensions: Button = findViewById(R.id.buttonDimensions)

        // Create views for the weight chart and empty dimensions placeholder
        val weightChart = LineChart(this)
        val emptyView = View(this).apply {
            setBackgroundColor(Color.LTGRAY)
        }

        // Initially show nothing
        chartContainer.removeAllViews()

        // Button click listeners
        buttonWeightChart.setOnClickListener {
            chartContainer.removeAllViews()
            chartContainer.addView(weightChart)
            setupWeightChart(weightChart) // Configure and display the weight chart
        }

        buttonDimensions.setOnClickListener {
            chartContainer.removeAllViews()
            chartContainer.addView(emptyView) // Show placeholder for dimensions
        }
    }

    // Configure the weight chart
    private fun setupWeightChart(chart: LineChart) {
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    val entries = measurements.map { measurement ->
                        measurement.date?.let { Entry(it.toFloat(), measurement.weight?.toFloat() ?: 0f) }
                    }

                    val dataSet = LineDataSet(entries, "Weight Over Time").apply {
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        setDrawCircles(true)
                        setCircleColor(Color.RED)
                        setDrawValues(false)
                    }

                    val lineData = LineData(dataSet)

                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Weight Progression" }

                        xAxis.apply {
                            textColor = Color.BLACK
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            valueFormatter = DateAxisValueFormatter() // Use custom formatter
                        }

                        axisLeft.apply {
                            textColor = Color.BLACK
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                        }

                        axisRight.isEnabled = false
                        setNoDataText("No weight data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    chart.clear()
                    chart.setNoDataText("No weight data available.")
                }
            }
        }
    }

    // Custom value formatter for the X-axis to display dates
    private class DateAxisValueFormatter : ValueFormatter() {
        private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            return sdf.format(value.toLong())
        }
    }
}
