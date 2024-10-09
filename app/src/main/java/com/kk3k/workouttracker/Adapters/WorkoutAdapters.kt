package com.kk3k.workouttracker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Workout

class WorkoutAdapter : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var workouts = emptyList<Workout>()

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutInfo: TextView = itemView.findViewById(R.id.textViewWorkoutInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_item, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = workouts[position]
        holder.workoutInfo.text = "Workout: ${currentWorkout.uid}, Notes: ${currentWorkout.notes}"
    }

    override fun getItemCount(): Int = workouts.size

    fun submitList(workoutList: List<Workout>) {
        workouts = workoutList
        notifyDataSetChanged()
    }
}
