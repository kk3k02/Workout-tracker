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

class ExerciseAdapter(
    private var exerciseWithSeriesList: List<Pair<Exercise, List<Series>>>  // List of Pairs (Exercise, List of Series)
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // Set to track expanded positions
    private var expandedPositions = mutableSetOf<Int>()

    // ViewHolder class for holding the view elements for each item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val expandableLayout: ViewGroup = itemView.findViewById(R.id.expandableLayout)
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
        val (exercise, seriesList) = exerciseWithSeriesList[position]

        // Set exercise name
        holder.exerciseName.text = exercise.name

        // Check if the current position is expanded, if so, show the expandable layout
        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Clear previous series views
        holder.expandableLayout.removeAllViews()

        // Add series views dynamically when expanded
        if (isExpanded) {
            for ((index, series) in seriesList.withIndex()) {
                val seriesView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.series_item, holder.expandableLayout, false)

                val seriesInfo = seriesView.findViewById<TextView>(R.id.seriesInfo)
                seriesInfo.text = "Set ${index + 1}: Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"} kg"

                holder.expandableLayout.addView(seriesView)
            }
        }

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
    override fun getItemCount(): Int = exerciseWithSeriesList.size

    // Update the list with new data and notify the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newExerciseWithSeriesList: List<Pair<Exercise, List<Series>>>) {
        exerciseWithSeriesList = newExerciseWithSeriesList
        notifyDataSetChanged()
    }
}
