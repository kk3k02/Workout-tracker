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

class SeriesAdapter(
    private val seriesList: MutableList<Series>,  // List of series to display
    private val onDeleteSeries: (Series) -> Unit  // Callback function for deleting a series
) : RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>() {

    // ViewHolder class that holds the view for each item in the list
    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seriesInfo: TextView = itemView.findViewById(R.id.textViewSeriesInfo)  // TextView to display series details
        val deleteSeriesIcon: ImageView = itemView.findViewById(R.id.iconDeleteSeries)  // Icon for deleting a series
    }

    // Creates and returns the ViewHolder for each series item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        // Inflate the series_item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_item, parent, false)
        return SeriesViewHolder(view)
    }

    // Binds the data to each ViewHolder based on the position in the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series = seriesList[position]  // Get the current series object
        // Set the text for reps and weight, display "N/A" if weight is null
        holder.seriesInfo.text = "Reps: ${series.repetitions}, Weight: ${series.weight ?: "N/A"} kg"

        // Set the click listener for the delete icon to remove the series
        holder.deleteSeriesIcon.setOnClickListener {
            onDeleteSeries(series)  // Invoke the callback function to delete the series
        }
    }

    // Returns the total number of items (series) in the list
    override fun getItemCount(): Int {
        return seriesList.size
    }

    // Method to update the list of series when data changes
    @SuppressLint("NotifyDataSetChanged")
    fun updateSeriesList(newSeriesList: List<Series>) {
        seriesList.clear()  // Clear the current list
        seriesList.addAll(newSeriesList)  // Add the new list of series
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }
}
