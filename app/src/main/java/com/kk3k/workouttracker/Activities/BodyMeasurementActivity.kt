package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.History_BodyMeasurementAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import kotlinx.coroutines.launch

class BodyMeasurementActivity : AppCompatActivity() {

    // Lazy initialization of the ViewModel using the 'viewModels()' Kotlin property delegate
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)
        supportActionBar?.hide() // Hide the ActionBar for a clean UI

        // Initialize RecyclerView, set the layout manager, and attach the adapter to display the list of measurements
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBodyMeasurements)
        val adapter = History_BodyMeasurementAdapter(bodyMeasurementViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the "Add Measurement" button, which triggers the dialog to add a new body measurement
        val buttonAddMeasurement: Button = findViewById(R.id.buttonAddMeasurement)
        buttonAddMeasurement.setOnClickListener {
            showAddMeasurementDialog()  // Show the dialog to add a new measurement
        }

        // Subscribe to measurement updates from the ViewModel and update the adapter with the latest measurements
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                adapter.submitList(measurements)  // Update the RecyclerView when measurements change
            }
        }
    }

    // Function to show a dialog for adding new body measurement entries
    private fun showAddMeasurementDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_measurement, null)
        builder.setView(view)

        // Find and initialize the input fields from the dialog layout
        val editTextBiceps = view.findViewById<EditText>(R.id.editTextBiceps)
        val editTextTriceps = view.findViewById<EditText>(R.id.editTextTriceps)
        val editTextChest = view.findViewById<EditText>(R.id.editTextChest)
        val editTextWaist = view.findViewById<EditText>(R.id.editTextWaist)
        val editTextHips = view.findViewById<EditText>(R.id.editTextHips)
        val editTextThighs = view.findViewById<EditText>(R.id.editTextThighs)
        val editTextCalves = view.findViewById<EditText>(R.id.editTextCalves)
        val editTextWeight = view.findViewById<EditText>(R.id.editTextWeight)
        val editTextNote = view.findViewById<EditText>(R.id.editTextNote)  // Optional note input field

        // Define the behavior for the "Save" button in the dialog
        builder.setPositiveButton("DODAJ") { _, _ ->

            // Validate all input fields before saving the new measurement
            if (validateInputs(editTextBiceps, editTextTriceps, editTextChest, editTextWaist,
                    editTextHips, editTextThighs, editTextCalves, editTextWeight, editTextNote)) {

                // Create a new BodyMeasurement object with the input data
                val newMeasurement = BodyMeasurement(
                    date = System.currentTimeMillis(),  // Store the current time as the measurement date
                    biceps = editTextBiceps.text.toString().toIntOrNull(),  // Parse the input to an integer
                    triceps = editTextTriceps.text.toString().toIntOrNull(),
                    chest = editTextChest.text.toString().toIntOrNull(),
                    waist = editTextWaist.text.toString().toIntOrNull(),
                    hips = editTextHips.text.toString().toIntOrNull(),
                    thighs = editTextThighs.text.toString().toIntOrNull(),
                    calves = editTextCalves.text.toString().toIntOrNull(),
                    weight = editTextWeight.text.toString().toIntOrNull(),
                    notes = editTextNote.text.toString()  // Capture any notes entered
                )

                // Insert the new measurement into the database via the ViewModel
                lifecycleScope.launch {
                    bodyMeasurementViewModel.insertBodyMeasurement(newMeasurement)
                }
            } else {
                // Show an error message if the input is invalid
                Toast.makeText(this@BodyMeasurementActivity, "POPRAW DANE PRZED DODANIEM", Toast.LENGTH_SHORT).show()
            }
        }

        // Define the behavior for the "Cancel" button (dismiss the dialog)
        builder.setNegativeButton("COFNIJ", null)
        builder.show()  // Display the dialog to the user
    }

    // Function to validate all the input fields and highlight errors if any
    private fun validateInputs(vararg fields: EditText): Boolean {
        var isValid = true
        var atLeastOneValidField = false  // Flag to ensure at least one valid field is filled

        // Iterate through each field to check for validity
        fields.forEach { field ->
            field.setBackgroundResource(android.R.drawable.edit_text)  // Reset background to default

            // If the field contains invalid data (empty or non-numeric input)
            if (field.text.isNotEmpty() && (field.id != R.id.editTextNote && !isValidInteger(field.text.toString()))) {
                field.setBackgroundResource(R.drawable.error_background)  // Highlight the field with an error
                isValid = false
            } else if (field.text.isNotEmpty() && field.id != R.id.editTextNote) {
                atLeastOneValidField = true  // Ensure at least one valid measurement is provided
            }
        }

        // Ensure that at least one valid measurement field is filled
        if (!atLeastOneValidField) {
            Toast.makeText(this@BodyMeasurementActivity, "At least one measurement must be filled", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    // Function to validate a single input field and update its background color in real-time
    private fun validateInputField(field: EditText) {
        if (field.id != R.id.editTextNote && field.text.isNotEmpty() && !isValidInteger(field.text.toString())) {
            field.setBackgroundResource(R.drawable.error_background)  // Highlight with red if invalid
        } else {
            field.setBackgroundResource(android.R.drawable.edit_text)  // Reset to default background if valid
        }
    }

    // Helper function to check if a string can be parsed as a valid integer
    private fun isValidInteger(value: String): Boolean {
        return try {
            value.toInt()  // Attempt to parse the value as an integer
            true
        } catch (e: NumberFormatException) {
            false  // Return false if the value is not a valid integer
        }
    }

    // Function to populate the database with sample measurements for testing or demonstration purposes
    private fun addSampleMeasurements() {
        lifecycleScope.launch {
            val sampleMeasurements = listOf(
                // Example measurements to pre-populate the database
                BodyMeasurement(
                    date = System.currentTimeMillis() - 30 * 86400000L, // 30 days ago
                    biceps = 34,
                    triceps = 32,
                    chest = 100,
                    waist = 88,
                    hips = 95,
                    thighs = 60,
                    calves = 40,
                    weight = 75,
                    notes = "First measurement: training start"
                ),
                BodyMeasurement(
                    date = System.currentTimeMillis(), // Today
                    biceps = 41,
                    triceps = 35,
                    chest = 109,
                    waist = 87,
                    hips = 97,
                    thighs = 67,
                    calves = 46,
                    weight = 79,
                    notes = "Tenth measurement: progress"
                )
                // Additional measurements omitted for brevity
            )

            // Add each measurement to the database
            sampleMeasurements.forEach { measurement ->
                bodyMeasurementViewModel.insertBodyMeasurement(measurement)
            }
        }
    }

}
