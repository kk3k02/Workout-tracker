package com.kk3k.workouttracker.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Series

// Adapter for managing and displaying a list of series (sets of repetitions and weights)
class SeriesAdapter(
    private val seriesList: MutableList<Series>,  // List of series to display
    private val onDeleteSeries: (Series) -> Unit  // Callback function for deleting a series
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    // ViewHolder class to hold views for each series item in the RecyclerView
    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seriesInfo: TextView = itemView.findViewById(R.id.textViewSeriesInfo)  // TextView to display series details (reps and weight)
        val deleteSeriesIcon: ImageView = itemView.findViewById(R.id.iconDeleteSeries)  // Icon to delete a series
    }

    // Inflates the layout for each series item and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        // Inflate the layout for a series item and create the ViewHolder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_item, parent, false)
        return SeriesViewHolder(view)  // Return the ViewHolder with the inflated layout
    }

    // Binds the data to each ViewHolder based on the position in the series list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series = seriesList[position]  // Get the current series object from the list

        // Set the text for reps and weight, display "N/A" if weight is null
        holder.seriesInfo.text = "Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"} kg"

        // Set the click listener for the delete icon to remove the series from the list
        holder.deleteSeriesIcon.setOnClickListener {
            onDeleteSeries(series)  // Invoke the callback function to delete the series from the list
        }
    }

    // Returns the total number of items (series) in the list
    override fun getItemCount(): Int {
        return seriesList.size  // Return the size of the series list
    }
}