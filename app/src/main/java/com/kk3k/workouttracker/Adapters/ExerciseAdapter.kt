package com.kk3k.workouttracker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Exercise

class ExerciseAdapter(
    private val exerciseList: MutableList<Exercise>, // Lista ćwiczeń jako MutableList
    private val onExerciseDelete: (Exercise) -> Unit  // Callback do usuwania ćwiczenia
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // ViewHolder klasy trzymającej widok elementu listy
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val deleteExerciseIcon: ImageView = itemView.findViewById(R.id.deleteExerciseIcon) // Ikona kosza do usuwania
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.exerciseName.text = exercise.name

        // Obsługa kliknięcia ikony kosza
        holder.deleteExerciseIcon.setOnClickListener {
            onExerciseDelete(exercise)  // Wywołaj callback po kliknięciu ikony kosza
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    // Aktualizacja listy ćwiczeń
    fun updateList(newExerciseList: List<Exercise>) {
        exerciseList.clear()
        exerciseList.addAll(newExerciseList)
        notifyDataSetChanged()
    }
}
