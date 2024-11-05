package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Workout
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class History_WorkoutAdapter(
    private val onWorkoutClick: (Int) -> Unit,  // Callback for when a workout item is clicked
    private val onDeleteClick: (Workout) -> Unit  // Callback for delete button click
) : RecyclerView.Adapter<History_WorkoutAdapter.WorkoutViewHolder>() {

    // Holds the list of workouts to display in the adapter
    private var workouts = emptyList<Workout>()

    // ViewHolder class that represents each item in the workout list
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutInfo: TextView = itemView.findViewById(R.id.workoutInfo)  // Text view for workout info
        val deleteWorkoutIcon: ImageView = itemView.findViewById(R.id.deleteWorkoutIcon)  // Image view for delete icon
        val noteIcon: ImageView = itemView.findViewById(R.id.noteIcon)  // Image view for notes icon
    }

    // Inflate the layout for each workout list item and return a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_workout_item, parent, false)
        return WorkoutViewHolder(itemView)
    }

    // Bind data to each item (ViewHolder) in the workout list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts[position]

        // Format the workout date to "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentWorkout.date)

        // Format the workout duration in "MM:SS" or show "N/A" if duration is null or zero
        val formattedDuration = if (currentWorkout.duration != null && currentWorkout.duration!! > 0) {
            formatDuration(currentWorkout.duration!!)
        } else {
            "N/A"
        }

        // Display the workout date and duration in the TextView
        holder.workoutInfo.text = "Workout on $formattedDate - Duration: $formattedDuration"

        // Set up the click listener for the workout item
        holder.itemView.setOnClickListener {
            onWorkoutClick(currentWorkout.uid)  // Pass the workout ID to the click callback
        }

        // Set up the click listener for the delete icon
        holder.deleteWorkoutIcon.setOnClickListener {
            onDeleteClick(currentWorkout)  // Pass the workout to the delete callback
        }

        // Set up the click listener for the note icon
        holder.noteIcon.setOnClickListener {
            showNotePopup(holder.itemView.context, currentWorkout.notes ?: "No notes available.")
        }
    }

    // Helper function to format duration from milliseconds to "MM:SS"
    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Function to show the workout note in a popup dialog
    private fun showNotePopup(context: Context, note: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Workout Note")
        builder.setMessage(note)
        builder.setPositiveButton("Close", null)
        builder.show()
    }

    // Return the total number of workouts in the list
    override fun getItemCount(): Int = workouts.size

    // Update the list of workouts and notify the adapter of data changes
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(workoutList: List<Workout>) {
        workouts = workoutList
        notifyDataSetChanged()  // Notify adapter to rebind the data
    }
}
