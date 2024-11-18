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

    // List to hold body measurement data
    private var measurements = emptyList<BodyMeasurement>()

    // ViewHolder class for RecyclerView items
    class BodyMeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measurementDate: TextView = itemView.findViewById(R.id.measurementDate)
        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        // TextViews for displaying various body measurements
        val biceps: TextView = itemView.findViewById(R.id.textViewBiceps)
        val triceps: TextView = itemView.findViewById(R.id.textViewTriceps)
        val chest: TextView = itemView.findViewById(R.id.textViewChest)
        val waist: TextView = itemView.findViewById(R.id.textViewWaist)
        val hips: TextView = itemView.findViewById(R.id.textViewHips)
        val thighs: TextView = itemView.findViewById(R.id.textViewThighs)
        val calves: TextView = itemView.findViewById(R.id.textViewCalves)
        val weight: TextView = itemView.findViewById(R.id.textViewWeight)
    }

    // Inflating layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_body_measurement_item, parent, false)
        return BodyMeasurementViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BodyMeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentMeasurement.date ?: 0L))

        // Setting text for the measurement date
        holder.measurementDate.text = "Date: $formattedDate"
        // Set up delete button to remove measurement from database via ViewModel
        holder.deleteButton.setOnClickListener {
            viewModel.deleteBodyMeasurement(currentMeasurement)
            measurements = measurements.filter { it != currentMeasurement }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, measurements.size)
        }

        // Setting up individual measurement details
        holder.biceps.text = "Biceps: ${currentMeasurement.biceps ?: "N/A"} cm"
        holder.triceps.text = "Triceps: ${currentMeasurement.triceps ?: "N/A"} cm"
        holder.chest.text = "Chest: ${currentMeasurement.chest ?: "N/A"} cm"
        holder.waist.text = "Waist: ${currentMeasurement.waist ?: "N/A"} cm"
        holder.hips.text = "Hips: ${currentMeasurement.hips ?: "N/A"} cm"
        holder.thighs.text = "Thighs: ${currentMeasurement.thighs ?: "N/A"} cm"
        holder.calves.text = "Calves: ${currentMeasurement.calves ?: "N/A"} cm"
        holder.weight.text = "Weight: ${currentMeasurement.weight ?: "N/A"} kg"

        // Expandable layout for showing and hiding additional details
        holder.itemView.setOnClickListener {
            holder.expandableLayout.visibility = if (holder.expandableLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int = measurements.size

    // Updates the list with new data and notifies the RecyclerView to refresh
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(measurementList: List<BodyMeasurement>) {
        measurements = measurementList
        notifyDataSetChanged()
    }
}
