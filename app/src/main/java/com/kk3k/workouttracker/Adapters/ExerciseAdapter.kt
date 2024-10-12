package com.kk3k.workouttracker.Adapters

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
    private val seriesMap: MutableMap<Int, MutableList<Series>>, // Mapowanie ćwiczeń na serie
    private val onExerciseDelete: (Exercise) -> Unit, // Callback do usuwania ćwiczenia
    private val onAddSeries: (Exercise) -> Unit  // Callback do dodawania serii
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exerciseName)
        val deleteExerciseIcon: ImageView = itemView.findViewById(R.id.deleteExerciseIcon)
        val addSeriesIcon: ImageView = itemView.findViewById(R.id.addSeriesIcon)
        val recyclerViewSeries: RecyclerView = itemView.findViewById(R.id.recyclerViewSeries)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.exerciseName.text = exercise.name

        // Ustawianie adaptera dla listy serii
        val seriesList = seriesMap[exercise.uid] ?: mutableListOf()
        val seriesAdapter = SeriesAdapter(seriesList)
        holder.recyclerViewSeries.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerViewSeries.adapter = seriesAdapter

        // Obsługa kliknięcia ikony kosza
        holder.deleteExerciseIcon.setOnClickListener {
            onExerciseDelete(exercise)  // Wywołaj callback po kliknięciu ikony kosza
        }

        // Obsługa kliknięcia ikony plusa
        holder.addSeriesIcon.setOnClickListener {
            onAddSeries(exercise)  // Wywołaj callback po kliknięciu ikony plusa
        }

        // Pokaż/ukryj listę serii
        holder.recyclerViewSeries.visibility = if (seriesList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = exerciseList.size

    // Aktualizacja listy ćwiczeń
    fun updateList(newExerciseList: List<Exercise>) {
        exerciseList.clear()
        exerciseList.addAll(newExerciseList)
        notifyDataSetChanged()
    }

    // Aktualizacja listy serii dla danego ćwiczenia
    fun updateSeries(exerciseId: Int, seriesList: List<Series>) {
        seriesMap[exerciseId] = seriesList.toMutableList()
        notifyDataSetChanged()
    }
}
