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

    // Lazy initialization of the ViewModel using the 'viewModels()' Kotlin property delegate
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

        //bodyMeasurementViewModel.addSampleBodyMeasurements()

        // Initialize RecyclerView and set its layout manager and adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBodyMeasurements)
        val adapter = History_BodyMeasurementAdapter(bodyMeasurementViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup button to open a dialog for adding new measurements
        val buttonAddMeasurement: Button = findViewById(R.id.buttonAddMeasurement)
        buttonAddMeasurement.setOnClickListener {
            showAddMeasurementDialog()
        }

        // Subscribe to measurement updates from the ViewModel and update the adapter accordingly
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                adapter.submitList(measurements)
            }
        }
    }

    // Function to show a dialog that allows the user to add new body measurement entries
    private fun showAddMeasurementDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_add_measurement, null)
        builder.setView(view)

        // Find and initialize input fields from the dialog layout
        val editTextBiceps = view.findViewById<EditText>(R.id.editTextBiceps)
        val editTextTriceps = view.findViewById<EditText>(R.id.editTextTriceps)
        val editTextChest = view.findViewById<EditText>(R.id.editTextChest)
        val editTextWaist = view.findViewById<EditText>(R.id.editTextWaist)
        val editTextHips = view.findViewById<EditText>(R.id.editTextHips)
        val editTextThighs = view.findViewById<EditText>(R.id.editTextThighs)
        val editTextCalves = view.findViewById<EditText>(R.id.editTextCalves)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote) // Note input field

        // Define the behavior for the "Save" button in the dialog
        builder.setPositiveButton("Save") { _, _ ->
            // Create a new measurement object from the input data
            val newMeasurement = BodyMeasurement(
                date = System.currentTimeMillis(),  // Capture the current time as the date
                biceps = editTextBiceps.text.toString().toIntOrNull(),
                triceps = editTextTriceps.text.toString().toIntOrNull(),
                chest = editTextChest.text.toString().toIntOrNull(),
                waist = editTextWaist.text.toString().toIntOrNull(),
                hips = editTextHips.text.toString().toIntOrNull(),
                thighs = editTextThighs.text.toString().toIntOrNull(),
                calves = editTextCalves.text.toString().toIntOrNull(),
                weight = editTextWeight.text.toString().toIntOrNull(),
                notes = editTextNote.text.toString()  // Capture the optional note
            )

            // Save the new measurement through the ViewModel
            lifecycleScope.launch {
                bodyMeasurementViewModel.insertBodyMeasurement(newMeasurement)
            }
        }

        // Define the behavior for the "Cancel" button
        builder.setNegativeButton("Cancel", null) // Simply dismiss the dialog on cancel
        builder.show()  // Display the dialog to the user
    }
}
