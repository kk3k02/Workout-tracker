package com.kk3k.workouttracker.Activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kk3k.workouttracker.Adapters.History_ExerciseAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.viewmodel.SeriesViewModel
import kotlinx.coroutines.launch

class WorkoutHistoryDetailsActivity : AppCompatActivity() {

    // ViewModel to manage series-related data operations
    private val seriesViewModel: SeriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)
        supportActionBar?.hide() // Hide the ActionBar

        // Retrieve the workout ID passed through the intent
        val workoutId = intent.getIntExtra("WORKOUT_ID", -1)

        // Initialize RecyclerView to display exercises with their series
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercises)
        val adapter = History_ExerciseAdapter(
            emptyList(),  // Initialize with an empty list of exercises
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) }  // Set up the callback for showing exercise info
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)  // Use linear layout for the RecyclerView

        // Fetch series for the specific workout and update the RecyclerView
        lifecycleScope.launch {
            seriesViewModel.getSeriesForWorkout(workoutId).collect { exerciseWithSeriesList ->
                adapter.submitList(exerciseWithSeriesList)  // Update the adapter with the list of exercises and series
            }
        }
    }

    // Show a dialog with information about an exercise
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        textViewName.text = exercise.name
        textViewDescription.text = exercise.description

        if (exercise.image != null && exercise.image!!.isNotEmpty()) {
            Glide.with(this).load(exercise.image).into(imageView)  // Load the exercise image if available
        } else {
            imageView.setImageResource(android.R.drawable.ic_dialog_info)  // Default image if none exists
        }
        builder.setView(view)
        builder.setPositiveButton("ZAMKNIJ", null)
        builder.show()
    }
}