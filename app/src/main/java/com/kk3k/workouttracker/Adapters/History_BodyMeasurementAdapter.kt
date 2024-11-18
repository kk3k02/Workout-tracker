package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import com.kk3k.workouttracker.ViewModels.BodyMeasurementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class History_BodyMeasurementAdapter(private val viewModel: BodyMeasurementViewModel) : RecyclerView.Adapter<History_BodyMeasurementAdapter.BodyMeasurementViewHolder>() {

    // List to hold the body measurement data
    private var measurements = emptyList<BodyMeasurement>()

    // ViewHolder class to define and manage views for each RecyclerView item
    class BodyMeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measurementDate: TextView = itemView.findViewById(R.id.measurementDate) // Displays the date of the measurement
        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout) // Layout to show additional measurement details
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton) // Button to delete a measurement
        val biceps: TextView = itemView.findViewById(R.id.textViewBiceps) // Displays the biceps measurement
        val triceps: TextView = itemView.findViewById(R.id.textViewTriceps) // Displays the triceps measurement
        val chest: TextView = itemView.findViewById(R.id.textViewChest) // Displays the chest measurement
        val waist: TextView = itemView.findViewById(R.id.textViewWaist) // Displays the waist measurement
        val hips: TextView = itemView.findViewById(R.id.textViewHips) // Displays the hips measurement
        val thighs: TextView = itemView.findViewById(R.id.textViewThighs) // Displays the thighs measurement
        val calves: TextView = itemView.findViewById(R.id.textViewCalves) // Displays the calves measurement
        val weight: TextView = itemView.findViewById(R.id.textViewWeight) // Displays the weight measurement
        val note: TextView = itemView.findViewById(R.id.textViewNote) // Displays the note associated with the measurement
    }

    // Inflates the layout for each item in the RecyclerView and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_body_measurement_item, parent, false)
        return BodyMeasurementViewHolder(itemView)
    }

    // Binds data to the views in the ViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BodyMeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Format the date into a human-readable format
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentMeasurement.date ?: 0L))

        // Populate the views with data from the current measurement
        holder.measurementDate.text = "Date: $formattedDate"
        holder.biceps.text = "Biceps: ${currentMeasurement.biceps ?: "N/A"} cm"
        holder.triceps.text = "Triceps: ${currentMeasurement.triceps ?: "N/A"} cm"
        holder.chest.text = "Chest: ${currentMeasurement.chest ?: "N/A"} cm"
        holder.waist.text = "Waist: ${currentMeasurement.waist ?: "N/A"} cm"
        holder.hips.text = "Hips: ${currentMeasurement.hips ?: "N/A"} cm"
        holder.thighs.text = "Thighs: ${currentMeasurement.thighs ?: "N/A"} cm"
        holder.calves.text = "Calves: ${currentMeasurement.calves ?: "N/A"} cm"
        holder.weight.text = "Weight: ${currentMeasurement.weight ?: "N/A"} kg"
        holder.note.text = "Note: ${currentMeasurement.notes ?: "N/A"}" // Display the note or "N/A" if empty

        // Handle delete button clicks
        holder.deleteButton.setOnClickListener {
            viewModel.deleteBodyMeasurement(currentMeasurement) // Remove measurement from the database
            measurements = measurements.filter { it != currentMeasurement } // Update the list
            notifyItemRemoved(position) // Notify RecyclerView about the removal
            notifyItemRangeChanged(position, measurements.size) // Refresh the range of items
        }

        // Handle expanding/collapsing of additional details
        holder.itemView.setOnClickListener {
            holder.expandableLayout.visibility =
                if (holder.expandableLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = measurements.size

    // Updates the list with new data and notifies the RecyclerView to refresh
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(measurementList: List<BodyMeasurement>) {
        measurements = measurementList
        notifyDataSetChanged()
    }
}
