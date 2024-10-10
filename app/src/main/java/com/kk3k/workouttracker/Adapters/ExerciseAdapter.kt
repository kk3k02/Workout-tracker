package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Exercise

class ExerciseAdapter(private val exerciseList: List<Exercise>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // List of Series data and set to track expanded positions
    private var seriesList = emptyList<Series>()
    private var expandedPositions = mutableSetOf<Int>() // Set to track expanded positions

    // ViewHolder class for holding the view elements for each item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val expandableLayout: View = itemView.findViewById(R.id.expandableLayout)
        val repetitions: TextView = itemView.findViewById(R.id.textViewRepetitions)
        val weight: TextView = itemView.findViewById(R.id.textViewWeight)
    }

    // Method to inflate the item layout and create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    // Bind data to the ViewHolder and handle the expand/collapse logic
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val currentSeries = seriesList[position]
        val exercise = exerciseList.find { it.uid == currentSeries.exerciseId }

        // Set exercise name, repetitions, and weight
        holder.exerciseName.text = exercise?.name ?: "Unknown Exercise"
        holder.repetitions.text = "Repetitions: ${currentSeries.repetitions}"
        holder.weight.text = "Weight: ${currentSeries.weight ?: "N/A"} kg"

        // Check if the current position is expanded, if so, show the expandable layout
        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Handle the click event to expand or collapse the item
        holder.itemView.setOnClickListener {
            if (isExpanded) {
                expandedPositions.remove(position) // Collapse if already expanded
            } else {
                expandedPositions.add(position) // Expand if not expanded
            }
            notifyItemChanged(position) // Notify that the item has changed
        }
    }

    // Return the size of the list
    override fun getItemCount(): Int = seriesList.size

    // Update the list with new data and notify the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(series: List<Series>) {
        seriesList = series
        notifyDataSetChanged()
    }
}
