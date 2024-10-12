package com.kk3k.workouttracker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Series

class SeriesAdapter(
    private val seriesList: List<Series>
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seriesInfo: TextView = itemView.findViewById(R.id.seriesInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_item, parent, false)
        return SeriesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series = seriesList[position]
        holder.seriesInfo.text = "Set ${position + 1}: Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"}"
    }

    override fun getItemCount(): Int {
        return seriesList.size
    }
}
