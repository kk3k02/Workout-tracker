package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class History_BodyMeasurementAdapter : RecyclerView.Adapter<History_BodyMeasurementAdapter.BodyMeasurementViewHolder>() {

    private var measurements = emptyList<BodyMeasurement>() // List to hold body measurements
    private var expandedPositions = mutableSetOf<Int>() // Set to track expanded items

    // ViewHolder class to hold references to the views
    class BodyMeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measurementDate: TextView = itemView.findViewById(R.id.measurementDate)
        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout)
        val biceps: TextView = itemView.findViewById(R.id.textViewBiceps)
        val triceps: TextView = itemView.findViewById(R.id.textViewTriceps)
        val chest: TextView = itemView.findViewById(R.id.textViewChest)
        val waist: TextView = itemView.findViewById(R.id.textViewWaist)
        val hips: TextView = itemView.findViewById(R.id.textViewHips)
        val thighs: TextView = itemView.findViewById(R.id.textViewThighs)
        val calves: TextView = itemView.findViewById(R.id.textViewCalves)
        val weight: TextView = itemView.findViewById(R.id.textViewWeight)
    }

    // Inflate the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_body_measurement_item, parent, false)
        return BodyMeasurementViewHolder(itemView)
    }

    // Bind data to the ViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BodyMeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Format the date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentMeasurement.date ?: 0L))

        // Set date in the main tile
        holder.measurementDate.text = "Date: $formattedDate"

        // Set the values in the expandable section
        holder.biceps.text = "Biceps: ${currentMeasurement.biceps} cm"
        holder.triceps.text = "Triceps: ${currentMeasurement.triceps} cm"
        holder.chest.text = "Chest: ${currentMeasurement.chest} cm"
        holder.waist.text = "Waist: ${currentMeasurement.waist ?: "N/A"} cm"
        holder.hips.text = "Hips: ${currentMeasurement.hips ?: "N/A"} cm"
        holder.thighs.text = "Thighs: ${currentMeasurement.thighs ?: "N/A"} cm"
        holder.calves.text = "Calves: ${currentMeasurement.calves ?: "N/A"} cm"
        holder.weight.text = "Weight: ${currentMeasurement.weight ?: "N/A"} kg"

        // Set visibility based on whether the item is expanded or not
        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Handle item click to expand or collapse
        holder.itemView.setOnClickListener {
            if (isExpanded) {
                // If expanded, collapse it
                expandedPositions.remove(position)
            } else {
                // If collapsed, expand it
                expandedPositions.add(position)
            }
            notifyItemChanged(position) // Refresh the specific item
        }
    }

    // Return the total number of items
    override fun getItemCount(): Int = measurements.size

    // Submit new data and refresh the list
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(measurementList: List<BodyMeasurement>) {
        measurements = measurementList
        notifyDataSetChanged()
    }
}
