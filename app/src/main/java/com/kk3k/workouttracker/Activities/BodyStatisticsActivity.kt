package com.kk3k.workouttracker.Activities

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import kotlinx.coroutines.launch

class BodyStatisticsActivity : AppCompatActivity() {
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_statistics)

        val weightChart: LineChart = findViewById(R.id.weightChart)

        // Collect weight data from ViewModel's Flow and plot it on the chart
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

                    weightChart.apply {
                        data = lineData
                        description = Description().apply { text = "Weight Progression" }
                        xAxis.apply {
                            textColor = Color.BLACK
                            valueFormatter = DateAxisValueFormatter()
                            setDrawGridLines(false)
                        }
                        axisLeft.textColor = Color.BLACK
                        axisRight.isEnabled = false
                        setNoDataText("No weight data available.")
                        invalidate() // Refresh the chart
                    }
                } else {
                    weightChart.clear()
                    weightChart.setNoDataText("No weight data available.")
                }
            }
        }
    }

    // Custom value formatter for the X-axis to display dates
    private class DateAxisValueFormatter : com.github.mikephil.charting.formatter.ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(value.toLong()))
        }
    }
}
