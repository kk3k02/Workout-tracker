package com.kk3k.workouttracker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Series

class SeriesAdapter(
    private val seriesList: MutableList<Series>,
    private val onDeleteSeries: (Series) -> Unit  // Callback do usuwania serii
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seriesInfo: TextView = itemView.findViewById(R.id.textViewSeriesInfo)
        val deleteSeriesIcon: ImageView = itemView.findViewById(R.id.iconDeleteSeries)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_item, parent, false)
        return SeriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series = seriesList[position]
        holder.seriesInfo.text = "Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"} kg"

        // Obsługa kliknięcia ikony minus (usuwanie serii)
        holder.deleteSeriesIcon.setOnClickListener {
            onDeleteSeries(series)  // Wywołaj callback, aby usunąć serię
        }
    }

    override fun getItemCount(): Int {
        return seriesList.size
    }

    // Aktualizacja listy serii
    fun updateSeriesList(newSeriesList: List<Series>) {
        seriesList.clear()
        seriesList.addAll(newSeriesList)
        notifyDataSetChanged()
    }
}
