package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series

// Adapter for managing and displaying exercises along with their associated series
class History_ExerciseAdapter(
    private var exerciseWithSeriesList: List<Pair<Exercise, List<Series>>>,  // List of exercises with their series
    private val onInfoClick: (Exercise) -> Unit  // Callback for handling info button clicks on an exercise
) : RecyclerView.Adapter<History_ExerciseAdapter.ExerciseViewHolder>() {

    // A set to track the expanded positions for exercises to show/hide their series
    private var expandedPositions = mutableSetOf<Int>()

    // ViewHolder class to define and manage views for each exercise item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)  // Displays the name of the exercise
        val infoIcon: ImageView = itemView.findViewById(R.id.infoIcon)  // Icon to show more information about the exercise
        val expandableLayout: ViewGroup = itemView.findViewById(R.id.expandableLayout)  // Layout that contains the series information
    }

    // This method inflates the item layout for each exercise and returns the corresponding ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    // This method binds the exercise data to the ViewHolder and sets up the interaction behaviors
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val (exercise, seriesList) = exerciseWithSeriesList[position]  // Get the exercise and its series at the current position
        holder.exerciseName.text = exercise.name  // Set the exercise name

        // Set up the info icon click listener to show more information about the exercise
        holder.infoIcon.setOnClickListener {
            onInfoClick(exercise)  // Trigger the info callback passing the current exercise
        }

        // Determine if the current exercise item is expanded (i.e., showing the series details)
        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE  // Show/hide the series details

        holder.expandableLayout.removeAllViews()  // Remove any existing series views to avoid duplication

        // If the exercise is expanded, display its series (sets of repetitions and weights)
        if (isExpanded) {
            // Loop through the series and display them in the expandable layout
            for ((index, series) in seriesList.withIndex()) {
                val seriesView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.history_series_item, holder.expandableLayout, false)  // Inflate layout for each series

                val seriesInfo = seriesView.findViewById<TextView>(R.id.seriesInfo)

                seriesInfo.setTextAppearance(R.style.SeriesInfoTextStyle) // For newer Android versions

                // Display the series details (repetitions and weight)
                seriesInfo.text = "${index + 1} Seria: Powtórzenia: ${series.repetitions}, Obciążenie: ${series.weight ?: "N/A"} kg"
                holder.expandableLayout.addView(seriesView)  // Add the series view to the expandable layout
            }
        }

        // Set up a click listener to expand or collapse the series details when the exercise item is clicked
        holder.itemView.setOnClickListener {
            // Toggle the expanded state for the current position
            if (isExpanded) {
                expandedPositions.remove(position)  // Remove from expanded positions if it's already expanded
            } else {
                expandedPositions.add(position)  // Add to expanded positions if it's collapsed
            }
            // Notify the adapter to refresh the item at the current position to reflect the change
            notifyItemChanged(position)
        }
    }

    // Returns the total number of items in the list (exercises with series)
    override fun getItemCount(): Int = exerciseWithSeriesList.size

    // Updates the list of exercises and series, and notifies the RecyclerView to refresh
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newExerciseWithSeriesList: List<Pair<Exercise, List<Series>>>) {
        exerciseWithSeriesList = newExerciseWithSeriesList  // Update the list with new data
        notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView
    }
}