package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import kotlinx.coroutines.launch

class BodyMeasurementActivity : AppCompatActivity() {

    // Lazy initialization of the ViewModel using the 'viewModels()' Kotlin property delegate
    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

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
                adapter.submitList(measurements)  // Update the RecyclerView with the latest measurements
            }
        }
    }

    // Function to show a dialog that allows the user to add new body measurement entries
    private fun showAddMeasurementDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
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

        // Real-time validation listeners for input fields
        val editTexts = listOf(editTextBiceps, editTextTriceps, editTextChest, editTextWaist,
            editTextHips, editTextThighs, editTextCalves, editTextWeight)

        // Attach TextWatcher for real-time validation
        editTexts.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Validate the field on each text change
                    validateInputField(editText)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        // Define the behavior for the "Save" button in the dialog
        builder.setPositiveButton("Save") { _, _ ->

            // Validate all fields before saving
            if (validateInputs(editTextBiceps, editTextTriceps, editTextChest, editTextWaist,
                    editTextHips, editTextThighs, editTextCalves, editTextWeight, editTextNote)) {

                // Create a new measurement object from the input data
                val newMeasurement = BodyMeasurement(
                    date = System.currentTimeMillis(),  // Capture the current time as the date
                    biceps = editTextBiceps.text.toString().toIntOrNull(),  // Parse the input as integers
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
            } else {
                Toast.makeText(this@BodyMeasurementActivity, "Please fix errors before saving", Toast.LENGTH_SHORT).show()
            }
        }

        // Define the behavior for the "Cancel" button
        builder.setNegativeButton("Cancel", null) // Simply dismiss the dialog on cancel
        builder.show()  // Display the dialog to the user
    }

    // Function to validate all the inputs and highlight errors if any
    private fun validateInputs(vararg fields: EditText): Boolean {
        var isValid = true
        var atLeastOneValidField = false  // Flag to check if at least one measurement is provided

        fields.forEach { field ->
            // Reset background to default
            field.setBackgroundResource(android.R.drawable.edit_text);

            // If the field is empty or not a valid number (except for notes)
            if (field.text.isNotEmpty() && (field.id != R.id.editTextNote && !isValidInteger(field.text.toString()))) {
                field.setBackgroundResource(R.drawable.error_background)  // Set red background for errors
                isValid = false
            } else if (field.text.isNotEmpty() && field.id != R.id.editTextNote) {
                atLeastOneValidField = true  // Mark if at least one valid measurement is entered
            }
        }

        // Ensure at least one valid measurement field is filled
        if (!atLeastOneValidField) {
            Toast.makeText(this@BodyMeasurementActivity, "At least one measurement must be filled", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    // Function to validate an individual input field and change the background color in real-time
    private fun validateInputField(field: EditText) {
        if (field.id != R.id.editTextNote && field.text.isNotEmpty() && !isValidInteger(field.text.toString())) {
            field.setBackgroundResource(R.drawable.error_background)  // Set red background for errors
        } else {
            field.setBackgroundResource(android.R.drawable.edit_text)  // Reset to default if valid
        }
    }

    // Helper function to check if a string is a valid integer
    private fun isValidInteger(value: String): Boolean {
        return try {
            value.toInt() // Check if it's a valid integer
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}
