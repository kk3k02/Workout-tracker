package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.History_BodyMeasurementAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.viewmodel.BodyMeasurementViewModel
import kotlinx.coroutines.launch
import java.util.*

class BodyMeasurementActivity : AppCompatActivity() {

    // Using viewModels delegate to initialize the ViewModel
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBodyMeasurements)
        val adapter = History_BodyMeasurementAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Button to add a new measurement
        val buttonAddMeasurement: Button = findViewById(R.id.buttonAddMeasurement)
        buttonAddMeasurement.setOnClickListener {
            showAddMeasurementDialog()
        }

        // Collecting data from ViewModel using Flow and updating the adapter
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                adapter.submitList(measurements)
            }
        }
    }

    // Function to show dialog to add a new measurement
    private fun showAddMeasurementDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_add_measurement, null)
        builder.setView(view)

        val editTextBiceps = view.findViewById<EditText>(R.id.editTextBiceps)
        val editTextTriceps = view.findViewById<EditText>(R.id.editTextTriceps)
        val editTextChest = view.findViewById<EditText>(R.id.editTextChest)
        val editTextWaist = view.findViewById<EditText>(R.id.editTextWaist)
        val editTextHips = view.findViewById<EditText>(R.id.editTextHips)
        val editTextThighs = view.findViewById<EditText>(R.id.editTextThighs)
        val editTextCalves = view.findViewById<EditText>(R.id.editTextCalves)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Save") { _, _ ->
            // Fetch input values from dialog
            val biceps = editTextBiceps.text.toString().toIntOrNull()
            val triceps = editTextTriceps.text.toString().toIntOrNull()
            val chest = editTextChest.text.toString().toIntOrNull()
            val waist = editTextWaist.text.toString().toIntOrNull()
            val hips = editTextHips.text.toString().toIntOrNull()
            val thighs = editTextThighs.text.toString().toIntOrNull()
            val calves = editTextCalves.text.toString().toIntOrNull()
            val weight = editTextWeight.text.toString().toIntOrNull()

            // Create new BodyMeasurement object
            val newMeasurement = BodyMeasurement(
                date = System.currentTimeMillis(),
                biceps = biceps,
                triceps = triceps,
                chest = chest,
                waist = waist,
                hips = hips,
                thighs = thighs,
                calves = calves,
                weight = weight
            )

            // Save the new measurement in the database
            lifecycleScope.launch {
                bodyMeasurementViewModel.insertBodyMeasurement(newMeasurement)
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
