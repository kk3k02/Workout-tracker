package com.kk3k.workouttracker.Activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import com.kk3k.workouttracker.db.entities.BodyMeasurement
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
        supportActionBar?.hide() // Hide the ActionBar

        // Initialize the views for the buttons and chart container
        chartContainer = findViewById(R.id.chartContainer)
        buttonGeneral = findViewById(R.id.buttonGeneral)
        buttonWeightChart = findViewById(R.id.buttonWeightChart)
        buttonDimensions = findViewById(R.id.buttonDimensions)

        // Set the "General" button as the default active button
        deactivateButtons()
        buttonGeneral.isEnabled = false

        // Initially, show nothing in the chart container
        chartContainer.removeAllViews()

        // Set click listeners for the buttons to display different charts or information
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
            val weightChart = LineChart(this)
            chartContainer.addView(weightChart) // Add weight chart to container
            setupWeightChart(weightChart) // Configure and display the weight chart
        }

        buttonDimensions.setOnClickListener {
            deactivateButtons()
            buttonDimensions.isEnabled = false
            chartContainer.removeAllViews()
            val bodyMeasurementsChart = LineChart(this)
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
            // Collect body measurements and weight data from the ViewModel
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    // Get first and last measurement for each body part
                    val firstWeightMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestWeightMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstBicepsMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestBicepsMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstTricepsMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestTricepsMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstChestMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestChestMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstWaistMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestWaistMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstHipsMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestHipsMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstThighsMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestThighsMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }
                    val firstCalvesMeasurement = measurements.minByOrNull { it.date ?: Long.MAX_VALUE }
                    val latestCalvesMeasurement = measurements.maxByOrNull { it.date ?: Long.MIN_VALUE }

                    // Calculate the change for each body part (e.g. weight, biceps, triceps, etc.)
                    val weightChange = if (firstWeightMeasurement != null && latestWeightMeasurement != null) {
                        val firstWeight = firstWeightMeasurement.weight?.toFloat() ?: 0f
                        val latestWeight = latestWeightMeasurement.weight?.toFloat() ?: 0f
                        latestWeight - firstWeight
                    } else 0f

                    val bicepsChange = if (firstBicepsMeasurement != null && latestBicepsMeasurement != null) {
                        val firstBiceps = firstBicepsMeasurement.biceps?.toFloat() ?: 0f
                        val latestBiceps = latestBicepsMeasurement.biceps?.toFloat() ?: 0f
                        latestBiceps - firstBiceps
                    } else 0f

                    val tricepsChange = if (firstTricepsMeasurement != null && latestTricepsMeasurement != null) {
                        val firstTriceps = firstTricepsMeasurement.triceps?.toFloat() ?: 0f
                        val latestTriceps = latestTricepsMeasurement.triceps?.toFloat() ?: 0f
                        latestTriceps - firstTriceps
                    } else 0f

                    val chestChange = if (firstChestMeasurement != null && latestChestMeasurement != null) {
                        val firstChest = firstChestMeasurement.chest?.toFloat() ?: 0f
                        val latestChest = latestChestMeasurement.chest?.toFloat() ?: 0f
                        latestChest - firstChest
                    } else 0f

                    val waistChange = if (firstWaistMeasurement != null && latestWaistMeasurement != null) {
                        val firstWaist = firstWaistMeasurement.waist?.toFloat() ?: 0f
                        val latestWaist = latestWaistMeasurement.waist?.toFloat() ?: 0f
                        latestWaist - firstWaist
                    } else 0f

                    val hipsChange = if (firstHipsMeasurement != null && latestHipsMeasurement != null) {
                        val firstHips = firstHipsMeasurement.hips?.toFloat() ?: 0f
                        val latestHips = latestHipsMeasurement.hips?.toFloat() ?: 0f
                        latestHips - firstHips
                    } else 0f

                    val thighsChange = if (firstThighsMeasurement != null && latestThighsMeasurement != null) {
                        val firstThighs = firstThighsMeasurement.thighs?.toFloat() ?: 0f
                        val latestThighs = latestThighsMeasurement.thighs?.toFloat() ?: 0f
                        latestThighs - firstThighs
                    } else 0f

                    val calvesChange = if (firstCalvesMeasurement != null && latestCalvesMeasurement != null) {
                        val firstCalves = firstCalvesMeasurement.calves?.toFloat() ?: 0f
                        val latestCalves = latestCalvesMeasurement.calves?.toFloat() ?: 0f
                        latestCalves - firstCalves
                    } else 0f

                    // Create the display text for each measurement showing differences
                    val generalTextView = TextView(this@BodyStatisticsActivity).apply {
                        textSize = 18f
                        setTextColor(Color.WHITE) // Ustaw biały tekst dla ciemnego tła
                        gravity = Gravity.CENTER
                        setPadding(16, 16, 16, 16) // Dodaj odstępy dla lepszej czytelności
                        background = ContextCompat.getDrawable(context, R.drawable.rounded_bg) // Opcjonalnie: zaokrąglone tło

                        // Zastosuj formatowanie HTML
                        text = """
        <b>Waga:</b> ${if (weightChange >= 0) "<font color='#00FF00'>+${weightChange.toInt()} kg</font>" else "<font color='#FF0000'>${weightChange.toInt()} kg</font>"}<br>
        <b>Biceps:</b> ${if (bicepsChange >= 0) "<font color='#00FF00'>+${bicepsChange.toInt()} cm</font>" else "<font color='#FF0000'>${bicepsChange.toInt()} cm</font>"}<br>
        <b>Triceps:</b> ${if (tricepsChange >= 0) "<font color='#00FF00'>+${tricepsChange.toInt()} cm</font>" else "<font color='#FF0000'>${tricepsChange.toInt()} cm</font>"}<br>
        <b>Klatka piersiowa:</b> ${if (chestChange >= 0) "<font color='#00FF00'>+${chestChange.toInt()} cm</font>" else "<font color='#FF0000'>${chestChange.toInt()} cm</font>"}<br>
        <b>Nadgarstek:</b> ${if (waistChange >= 0) "<font color='#00FF00'>+${waistChange.toInt()} cm</font>" else "<font color='#FF0000'>${waistChange.toInt()} cm</font>"}<br>
        <b>Biodra:</b> ${if (hipsChange >= 0) "<font color='#00FF00'>+${hipsChange.toInt()} cm</font>" else "<font color='#FF0000'>${hipsChange.toInt()} cm</font>"}<br>
        <b>Uda:</b> ${if (thighsChange >= 0) "<font color='#00FF00'>+${thighsChange.toInt()} cm</font>" else "<font color='#FF0000'>${thighsChange.toInt()} cm</font>"}<br>
        <b>Łydki:</b> ${if (calvesChange >= 0) "<font color='#00FF00'>+${calvesChange.toInt()} cm</font>" else "<font color='#FF0000'>${calvesChange.toInt()} cm</font>"}<br>
    """.trimIndent().let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) } // Konwertuj HTML na format tekstowy
                    }

                    // Display the result in the chart container
                    chartContainer.addView(generalTextView)
                } else {
                    // If no measurements are available
                    val generalTextView = TextView(this@BodyStatisticsActivity).apply {
                        text = "Brak pomiarów."
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
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    val firstDate = measurements.minOfOrNull { it.date ?: Long.MAX_VALUE }?.toFloat() ?: 0f
                    val minWeight = measurements.mapNotNull { it.weight?.toFloat() }.minOrNull() ?: 0f

                    val entries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { Entry(it.toFloat(), measurement.weight?.toFloat() ?: 0f) }
                    }

                    val dataSet = LineDataSet(entries, "Waga użytkownika w czasie").apply {
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        setDrawCircles(true)
                        setCircleColor(Color.RED)
                        circleRadius = 4f
                        setDrawValues(false)
                    }

                    val lineData = LineData(dataSet)

                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Zmiana Wagi użytkownika" }

                        xAxis.apply {
                            textColor = Color.WHITE
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            labelCount = 3
                            valueFormatter = DateAxisValueFormatter()
                            axisMinimum = firstDate
                            textSize = 12f
                        }

                        axisLeft.apply {
                            textColor = Color.WHITE
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            axisMinimum = minWeight
                            textSize = 12f
                            labelCount = 6
                            valueFormatter = YAxisValueFormatter("kg")
                        }

                        axisRight.isEnabled = false

                        legend.apply {
                            textColor = Color.WHITE
                            form = Legend.LegendForm.LINE
                        }

                        setNoDataText("Brak dostępnych pomiarów.")
                        invalidate()
                    }
                } else {
                    chart.clear()
                    chart.setNoDataText("Brak dostępnych pomiarów wagi.")
                }
            }
        }
    }

    // Configure the body measurements chart to display multiple datasets
    private fun setupBodyMeasurementsChart(chart: LineChart) {
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    val minMeasurement = measurements.flatMap {
                        listOfNotNull(it.biceps, it.triceps, it.chest, it.waist, it.hips, it.thighs, it.calves)
                    }.minOrNull()?.toFloat() ?: 0f

                    val dataSets = mutableListOf<LineDataSet>()

                    // Mapowanie danych dla wymiarów ciała
                    val bicepsEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.biceps?.toFloat() ?: 0f).apply {
                            data = measurement // Przechowujemy cały obiekt BodyMeasurement w Entry.data
                        }}
                    }

                    val tricepsEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.triceps?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    val chestEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.chest?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    // Dodajemy inne wymiary (waist, hips, thighs, calves) w podobny sposób
                    val waistEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.waist?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    val hipsEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.hips?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    val thighsEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.thighs?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    val calvesEntries = measurements.mapNotNull { measurement ->
                        measurement.date?.let { date -> Entry(date.toFloat(), measurement.calves?.toFloat() ?: 0f).apply {
                            data = measurement
                        }}
                    }

                    // Tworzymy zestawy danych dla wykresu
                    dataSets.add(createDataSet(bicepsEntries, "Biceps", Color.RED))
                    dataSets.add(createDataSet(tricepsEntries, "Triceps", Color.GREEN))
                    dataSets.add(createDataSet(chestEntries, "Klatka piersiowa", Color.BLUE))
                    dataSets.add(createDataSet(waistEntries, "Nadgarstki", Color.YELLOW))
                    dataSets.add(createDataSet(hipsEntries, "Biodra", Color.MAGENTA))
                    dataSets.add(createDataSet(thighsEntries, "Uda", Color.CYAN))
                    dataSets.add(createDataSet(calvesEntries, "Łydki", Color.GRAY))

                    val lineData = LineData(dataSets as List<ILineDataSet>)

                    chart.apply {
                        data = lineData
                        description = Description().apply { text = "Zmiana wymiarów ciała" }

                        xAxis.apply {
                            textColor = Color.WHITE
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            labelCount = 3
                            valueFormatter = DateAxisValueFormatter() // Formatowanie dat na osi X
                            textSize = 12f
                        }

                        axisLeft.apply {
                            textColor = Color.WHITE
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                            axisMinimum = minMeasurement
                            textSize = 12f
                            labelCount = 6
                            valueFormatter = YAxisValueFormatter("cm") // Jednostka "cm"
                        }

                        axisRight.isEnabled = false

                        legend.apply {
                            isEnabled = true
                            textColor = Color.WHITE
                            form = Legend.LegendForm.LINE
                        }

                        setNoDataText("Brak dostępnych pomiarów.")

                        // Obsługa kliknięć na punkty wykresu
                        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                if (e != null && e.data is BodyMeasurement) {
                                    val measurement = e.data as BodyMeasurement
                                    val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(measurement.date ?: 0L)

                                    // Tworzymy wiadomość z wszystkimi wymiarami dla danego punktu
                                    val message = """
                                    Data: $selectedDate
                                    Biceps: ${measurement.biceps ?: "N/A"} cm
                                    Triceps: ${measurement.triceps ?: "N/A"} cm
                                    Klatka piersiowa: ${measurement.chest ?: "N/A"} cm
                                    Nadgarstek: ${measurement.waist ?: "N/A"} cm
                                    Biodra: ${measurement.hips ?: "N/A"} cm
                                    Uda: ${measurement.thighs ?: "N/A"} cm
                                    Łydki: ${measurement.calves ?: "N/A"} cm
                                """.trimIndent()

                                    // Wyświetlamy dialog z informacjami
                                    AlertDialog.Builder(this@BodyStatisticsActivity)
                                        .setTitle("Szczegóły pomiarów")
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            }

                            override fun onNothingSelected() {
                                // Nic nie robi, gdy nic nie jest wybrane
                            }
                        })

                        invalidate() // Odśwież wykres
                    }
                } else {
                    chart.clear()
                    chart.setNoDataText("Brak dostępnych pomiarów.")
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