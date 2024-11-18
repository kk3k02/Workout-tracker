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
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import kotlinx.coroutines.launch

class BodyMeasurementActivity : AppCompatActivity() {

    // ViewModel for handling operations related to body measurements
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

        // Setting up the RecyclerView for displaying body measurements
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBodyMeasurements)
        val adapter = History_BodyMeasurementAdapter(bodyMeasurementViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Button to trigger adding a new measurement
        val buttonAddMeasurement: Button = findViewById(R.id.buttonAddMeasurement)
        buttonAddMeasurement.setOnClickListener {
            showAddMeasurementDialog()
        }

        // Observing measurements data from the ViewModel and updating the UI accordingly
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                adapter.submitList(measurements)
            }
        }
    }

    // Function to display a dialog allowing the user to add a new body measurement
    private fun showAddMeasurementDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_add_measurement, null)
        builder.setView(view)

        // Getting references to input fields in the dialog
        val editTextBiceps = view.findViewById<EditText>(R.id.editTextBiceps)
        val editTextTriceps = view.findViewById<EditText>(R.id.editTextTriceps)
        val editTextChest = view.findViewById<EditText>(R.id.editTextChest)
        val editTextWaist = view.findViewById<EditText>(R.id.editTextWaist)
        val editTextHips = view.findViewById<EditText>(R.id.editTextHips)
        val editTextThighs = view.findViewById<EditText>(R.id.editTextThighs)
        val editTextCalves = view.findViewById<EditText>(R.id.editTextCalves)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)

        // Setting up the response to the "Save" button in the dialog
        builder.setPositiveButton("Save") { _, _ ->
            // Parsing user input to create a new BodyMeasurement instance
            val newMeasurement = BodyMeasurement(
                date = System.currentTimeMillis(),
                biceps = editTextBiceps.text.toString().toIntOrNull(),
                triceps = editTextTriceps.text.toString().toIntOrNull(),
                chest = editTextChest.text.toString().toIntOrNull(),
                waist = editTextWaist.text.toString().toIntOrNull(),
                hips = editTextHips.text.toString().toIntOrNull(),
                thighs = editTextThighs.text.toString().toIntOrNull(),
                calves = editTextCalves.text.toString().toIntOrNull(),
                weight = editTextWeight.text.toString().toIntOrNull()
            )

            // Saving the new measurement to the database via the ViewModel
            lifecycleScope.launch {
                bodyMeasurementViewModel.insertBodyMeasurement(newMeasurement)
            }
        }

        // Handling the "Cancel" button in the dialog
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
