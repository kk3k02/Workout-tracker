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

class ExerciseAdapter(
    private val exerciseList: MutableList<Exercise>,
    private val seriesMap: MutableMap<Int, MutableList<Series>>, // Mapping exercises to their series
    private val onExerciseDelete: (Exercise) -> Unit, // Callback to delete exercise
    private val onAddSeries: (Exercise) -> Unit,  // Callback to add a series
    private val onDeleteSeries: (Series) -> Unit  // Callback to delete a series
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // ViewHolder class to hold the views for each exercise item
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName) // Text for exercise name
        val deleteExerciseIcon: ImageView = itemView.findViewById(R.id.deleteExerciseIcon) // Icon to delete the exercise
        val addSeriesIcon: ImageView = itemView.findViewById(R.id.addSeriesIcon) // Icon to add a series
        val recyclerViewSeries: RecyclerView = itemView.findViewById(R.id.recyclerViewSeries) // RecyclerView to display series
    }

    // Method to inflate the item layout for the exercise
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    // Bind exercise data and handle click actions for add/delete series and exercise
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.exerciseName.text = exercise.name

        // Set up the adapter for the list of series related to the exercise
        val seriesList = seriesMap[exercise.uid] ?: mutableListOf()
        val seriesAdapter = SeriesAdapter(seriesList, onDeleteSeries = { series ->
            onDeleteSeries(series) // Call the delete series callback when clicked
        })
        holder.recyclerViewSeries.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerViewSeries.adapter = seriesAdapter

        // Handle click for deleting the exercise
        holder.deleteExerciseIcon.setOnClickListener {
            onExerciseDelete(exercise) // Call the delete exercise callback
        }

        // Handle click for adding a new series
        holder.addSeriesIcon.setOnClickListener {
            onAddSeries(exercise) // Call the add series callback
        }

        // Show the series list if there are series, hide it otherwise
        holder.recyclerViewSeries.visibility = if (seriesList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Return the number of exercises in the list
    override fun getItemCount(): Int = exerciseList.size

    // Update the exercise list with new data
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newExerciseList: List<Exercise>) {
        exerciseList.clear()
        exerciseList.addAll(newExerciseList)
        notifyDataSetChanged() // Notify adapter to refresh the list
    }

    // Update the list of series for a specific exercise
    @SuppressLint("NotifyDataSetChanged")
    fun updateSeries(exerciseId: Int, seriesList: List<Series>) {
        seriesMap[exerciseId] = seriesList.toMutableList() // Update the map with the new series list
        notifyDataSetChanged() // Notify adapter to refresh the list
    }
}
