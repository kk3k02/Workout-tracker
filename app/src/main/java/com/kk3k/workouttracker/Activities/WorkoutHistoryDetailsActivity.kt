package com.kk3k.workouttracker.Activities

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

    private val seriesViewModel: SeriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        val workoutId = intent.getIntExtra("WORKOUT_ID", -1)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewExercises)
        val adapter = History_ExerciseAdapter(
            emptyList(),
            onInfoClick = { exercise -> showExerciseInfoDialog(exercise) }  // Pass the info click callback
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            seriesViewModel.getSeriesForWorkout(workoutId).collect { exerciseWithSeriesList ->
                adapter.submitList(exerciseWithSeriesList)
            }
        }
    }

    // Function to display exercise info dialog
    private fun showExerciseInfoDialog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_exercise_info, null)

        val imageView = view.findViewById<ImageView>(R.id.imageViewExercise)
        val textViewName = view.findViewById<TextView>(R.id.textViewExerciseName)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewExerciseDescription)

        // Load the GIF or set default image if no image
        if (exercise.image != null) {
            Glide.with(this).asGif().load(exercise.image).into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_dialog_info)
        }

        textViewName.text = exercise.name
        textViewDescription.text = exercise.description

        builder.setView(view)
        builder.setPositiveButton("Close", null)
        builder.show()
    }
}
