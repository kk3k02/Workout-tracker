package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Workout
import java.text.SimpleDateFormat
import java.util.Locale

class History_WorkoutAdapter(
    private val onWorkoutClick: (Int) -> Unit,  // Callback for workout click
    private val onDeleteClick: (Workout) -> Unit  // Callback for delete button click
) : RecyclerView.Adapter<History_WorkoutAdapter.WorkoutViewHolder>() {

    // Holds the list of workouts to display
    private var workouts = emptyList<Workout>()

    // ViewHolder class for managing workout list item views
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutInfo: TextView = itemView.findViewById(R.id.workoutInfo)
        val deleteWorkoutIcon: ImageView = itemView.findViewById(R.id.deleteWorkoutIcon)  // Reference to the delete button
    }

    // Inflate the workout list item layout and return a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_workout_item, parent, false)
        return WorkoutViewHolder(itemView)
    }

    // Bind data to the ViewHolder for each item in the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts[position]

        // Format the workout date as dd/MM/yyyy
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentWorkout.date)

        // Display the workout date and duration in minutes
        holder.workoutInfo.text = "Workout $formattedDate - ${currentWorkout.duration} minutes"

        // Set click listener to handle when a workout is clicked
        holder.itemView.setOnClickListener {
            onWorkoutClick(currentWorkout.uid) // Pass the workout ID on click
        }

        // Set click listener for the delete icon
        holder.deleteWorkoutIcon.setOnClickListener {
            onDeleteClick(currentWorkout)  // Call the delete callback
        }
    }

    // Return the total number of workouts in the list
    override fun getItemCount(): Int = workouts.size

    // Update the list of workouts and refresh the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(workoutList: List<Workout>) {
        workouts = workoutList
        notifyDataSetChanged() // Notify adapter to rebind the data
    }
}
