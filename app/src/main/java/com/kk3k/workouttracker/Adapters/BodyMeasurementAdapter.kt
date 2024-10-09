package com.kk3k.workouttracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.db.entities.BodyMeasurement

class BodyMeasurementAdapter : RecyclerView.Adapter<BodyMeasurementAdapter.BodyMeasurementViewHolder>() {

    private var measurements = emptyList<BodyMeasurement>()

    class BodyMeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measurementInfo: TextView = itemView.findViewById(R.id.textViewMeasurementInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_measurement_item, parent, false)
        return BodyMeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BodyMeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]
        holder.measurementInfo.text = "Date: ${currentMeasurement.date}, Weight: ${currentMeasurement.weight} kg"
    }

    override fun getItemCount(): Int = measurements.size

    fun submitList(measurementList: List<BodyMeasurement>) {
        measurements = measurementList
        notifyDataSetChanged()
    }
}
