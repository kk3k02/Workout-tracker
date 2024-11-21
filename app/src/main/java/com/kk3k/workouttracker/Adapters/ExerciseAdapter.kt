package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series

// Adapter for managing and displaying exercises along with their series
class ExerciseAdapter(
    private val exerciseList: MutableList<Exercise>,  // List of exercises to be displayed
    private val seriesMap: MutableMap<Int, MutableList<Series>>, // Mapping exercises to their series
    private val onExerciseDelete: (Exercise) -> Unit, // Callback to delete exercise
    private val onAddSeries: (Exercise) -> Unit,  // Callback to add a series
    private val onDeleteSeries: (Series) -> Unit,  // Callback to delete a series
    private val onInfoClick: (Exercise) -> Unit  // Callback to show exercise info
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // ViewHolder class to hold the views for each exercise item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName) // Text for exercise name
        val deleteExerciseIcon: ImageView = itemView.findViewById(R.id.deleteExerciseIcon) // Icon to delete the exercise
        val addSeriesIcon: ImageView = itemView.findViewById(R.id.addSeriesIcon) // Icon to add a series
        val infoExerciseIcon: ImageView = itemView.findViewById(R.id.infoExerciseIcon) // Icon to show exercise info
        val recyclerViewSeries: RecyclerView = itemView.findViewById(R.id.recyclerViewSeries) // RecyclerView to display series
    }

    // Method to inflate the item layout for the exercise and create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        // Inflate the exercise item layout and return the ViewHolder
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    // Bind exercise data to the ViewHolder and set up click actions
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]  // Get the exercise at the current position
        holder.exerciseName.text = exercise.name  // Set the exercise name in the view

        // Get the series associated with this exercise from the seriesMap
        val seriesList = seriesMap[exercise.uid] ?: mutableListOf()

        // Set up the adapter for the RecyclerView displaying the series of the exercise
        val seriesAdapter = SeriesAdapter(seriesList, onDeleteSeries = { series ->
            onDeleteSeries(series) // Call the delete series callback when a series is clicked
        })
        holder.recyclerViewSeries.layoutManager = LinearLayoutManager(holder.itemView.context)  // Set the layout manager for series RecyclerView
        holder.recyclerViewSeries.adapter = seriesAdapter  // Set the series adapter

        // Handle click for deleting the exercise
        holder.deleteExerciseIcon.setOnClickListener {
            onExerciseDelete(exercise)  // Call the delete exercise callback
        }

        // Handle click for adding a new series to the exercise
        holder.addSeriesIcon.setOnClickListener {
            onAddSeries(exercise)  // Call the add series callback
        }

        // Handle click for showing more information about the exercise
        holder.infoExerciseIcon.setOnClickListener {
            onInfoClick(exercise)  // Call the show exercise info callback
        }

        // If there are series for this exercise, show the RecyclerView, otherwise hide it
        holder.recyclerViewSeries.visibility = if (seriesList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Return the number of exercises in the list
    override fun getItemCount(): Int = exerciseList.size

    // Update the series list for a specific exercise and refresh the RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun updateSeries(exerciseId: Int, seriesList: List<Series>) {
        seriesMap[exerciseId] = seriesList.toMutableList()  // Update the seriesMap with the new list of series
        notifyDataSetChanged()  // Notify the adapter to refresh the list
    }
}