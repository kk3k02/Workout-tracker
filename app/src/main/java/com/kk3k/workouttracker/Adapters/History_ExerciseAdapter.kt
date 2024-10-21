package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.Activities.WorkoutActivity // Use the function from WorkoutActivity

class History_ExerciseAdapter(
    private var exerciseWithSeriesList: List<Pair<Exercise, List<Series>>>,
    private val onInfoClick: (Exercise) -> Unit  // Pass the callback for the info button
) : RecyclerView.Adapter<History_ExerciseAdapter.ExerciseViewHolder>() {

    private var expandedPositions = mutableSetOf<Int>()

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val infoIcon: ImageView = itemView.findViewById(R.id.infoIcon)  // Reference to the info button
        val expandableLayout: ViewGroup = itemView.findViewById(R.id.expandableLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val (exercise, seriesList) = exerciseWithSeriesList[position]
        holder.exerciseName.text = exercise.name

        // Info icon click listener
        holder.infoIcon.setOnClickListener {
            onInfoClick(exercise)  // Pass the exercise to the info callback
        }

        val isExpanded = expandedPositions.contains(position)
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.expandableLayout.removeAllViews()

        if (isExpanded) {
            for ((index, series) in seriesList.withIndex()) {
                val seriesView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.history_series_item, holder.expandableLayout, false)

                val seriesInfo = seriesView.findViewById<TextView>(R.id.seriesInfo)
                seriesInfo.text = "Set ${index + 1}: Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"} kg"
                holder.expandableLayout.addView(seriesView)
            }
        }

        holder.itemView.setOnClickListener {
            if (isExpanded) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = exerciseWithSeriesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newExerciseWithSeriesList: List<Pair<Exercise, List<Series>>>) {
        exerciseWithSeriesList = newExerciseWithSeriesList
        notifyDataSetChanged()
    }
}
