package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.util.Log
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

class BodyMeasurementAdapter : RecyclerView.Adapter<BodyMeasurementAdapter.BodyMeasurementViewHolder>() {

    private var measurements = emptyList<BodyMeasurement>()
    private var expandedPositions = mutableSetOf<Int>() // Zbiór do śledzenia rozwiniętych pozycji

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyMeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_measurement_item, parent, false)
        return BodyMeasurementViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BodyMeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Formatowanie daty
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentMeasurement.date ?: 0L))

        // Ustaw datę pomiaru w kafelku
        holder.measurementDate.text = "Date: $formattedDate"

        // Ustaw wartości w sekcji rozwijanej
        holder.biceps.text = "Biceps: ${currentMeasurement.biceps} cm"
        holder.triceps.text = "Triceps: ${currentMeasurement.triceps} cm"
        holder.chest.text = "Chest: ${currentMeasurement.chest} cm"
        holder.waist.text = "Waist: ${currentMeasurement.waist ?: "N/A"} cm"
        holder.hips.text = "Hips: ${currentMeasurement.hips ?: "N/A"} cm"
        holder.thighs.text = "Thighs: ${currentMeasurement.thighs ?: "N/A"} cm"
        holder.calves.text = "Calves: ${currentMeasurement.calves ?: "N/A"} cm"
        holder.weight.text = "Weight: ${currentMeasurement.weight ?: "N/A"} kg"


        // Ustal widoczność na podstawie stanu w expandedPositions
        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Dodaj logowanie do stanu rozwinięcia
        Log.d("BodyMeasurementAdapter", "Position: $position, isExpanded: $isExpanded")

        // Obsługa kliknięcia w kafelek
        holder.itemView.setOnClickListener {
            Log.d("BodyMeasurementAdapter", "Clicked on position: $position")

            if (isExpanded) {
                // Jeśli jest już rozwinięty, usuń pozycję ze zbioru
                expandedPositions.remove(position)
            } else {
                // Jeśli nie jest rozwinięty, dodaj pozycję do zbioru
                expandedPositions.add(position)
            }

            Log.d("BodyMeasurementAdapter", "New state: $expandedPositions")
            notifyItemChanged(position) // Odśwież tylko ten konkretny element
        }
    }


    override fun getItemCount(): Int = measurements.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(measurementList: List<BodyMeasurement>) {
        measurements = measurementList
        notifyDataSetChanged()
    }
}
