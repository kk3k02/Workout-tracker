package com.kk3k.workouttracker.Activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.ExerciseAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.ViewModels.ExerciseViewModel
import com.kk3k.workouttracker.db.entities.Exercise
import com.kk3k.workouttracker.db.entities.Series
import com.kk3k.workouttracker.db.entities.Workout
import com.kk3k.workouttracker.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    // Lista dodanych ćwiczeń do treningu
    private val selectedExercises = mutableListOf<Exercise>()
    private val seriesMap = mutableMapOf<Int, MutableList<Series>>() // Mapowanie ćwiczeń na serie

    // Adapter do RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    // Przechowywanie ID treningu w bazie danych
    private var workoutId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val buttonAddExercise: Button = findViewById(R.id.buttonAddExercise)
        val buttonSaveWorkout: Button = findViewById(R.id.buttonSaveWorkout)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewExercises)

        // Ustawienia RecyclerView
        exerciseAdapter = ExerciseAdapter(
            selectedExercises,
            seriesMap,
            onExerciseDelete = { exercise -> removeExercise(exercise) },
            onAddSeries = { exercise -> showAddSeriesDialog(exercise) },
            onDeleteSeries = { series -> removeSeries(series) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Obsługa kliknięcia na przycisk Add Exercise
        buttonAddExercise.setOnClickListener {
            showExerciseSelectionDialog()
        }

        // Obsługa kliknięcia na przycisk Save
        buttonSaveWorkout.setOnClickListener {
            saveWorkout()
        }

        // Tworzenie nowego treningu w bazie danych przy starcie aktywności
        createNewWorkout()
    }

    // Tworzenie nowego obiektu workout w bazie danych
    private fun createNewWorkout() {
        lifecycleScope.launch {
            val workout = Workout(
                date = System.currentTimeMillis(),
                duration = null,
                notes = "Workout in progress"
            )

            // Zapisz workout i ustaw jego ID
            workoutViewModel.insertWorkout(workout)
            workoutViewModel.getMostRecentWorkout { recentWorkout ->
                workoutId = recentWorkout?.uid
            }
        }
    }

    // Funkcja do wyświetlenia dialogu z listą ćwiczeń
    private fun showExerciseSelectionDialog() {
        val exerciseList = mutableListOf<Exercise>()

        // Pobieranie ćwiczeń z bazy danych
        lifecycleScope.launch {
            exerciseViewModel.allExercises.collect { exercises ->
                exerciseList.clear()
                exerciseList.addAll(exercises)

                // Wyświetlenie dialogu z listą ćwiczeń
                val exerciseNames = exerciseList.map { it.name }.toTypedArray()

                val builder = androidx.appcompat.app.AlertDialog.Builder(this@WorkoutActivity)
                builder.setTitle("Select Exercise")
                builder.setItems(exerciseNames) { _, which ->
                    val selectedExercise = exerciseList[which]
                    selectedExercises.add(selectedExercise)
                    exerciseAdapter.notifyDataSetChanged()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    // Funkcja do usuwania ćwiczenia
    private fun removeExercise(exercise: Exercise) {
        selectedExercises.remove(exercise)
        seriesMap.remove(exercise.uid) // Usuń również serie powiązane z ćwiczeniem
        exerciseAdapter.notifyDataSetChanged()
    }

    // Funkcja do usuwania serii
    private fun removeSeries(series: Series) {
        seriesMap[series.exerciseId]?.remove(series)
        exerciseAdapter.updateSeries(series.exerciseId, seriesMap[series.exerciseId] ?: mutableListOf())

        // Usuń serię z bazy danych
        workoutViewModel.deleteSeries(series)
    }

    // Funkcja do wyświetlenia dialogu do dodawania serii
    private fun showAddSeriesDialog(exercise: Exercise) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_series, null)
        builder.setView(view)

        val editTextReps = view.findViewById<android.widget.EditText>(R.id.editTextReps)
        val editTextWeight = view.findViewById<android.widget.EditText>(R.id.editTextWeight)

        builder.setPositiveButton("Add") { _, _ ->
            val reps = editTextReps.text.toString().toIntOrNull() ?: 0
            val weight = editTextWeight.text.toString().toFloatOrNull()

            // Dodaj nową serię do mapy i odśwież listę
            val newSeries = Series(
                workoutId = workoutId ?: 0,  // Użyj ID aktualnego workoutu
                exerciseId = exercise.uid,
                repetitions = reps,
                weight = weight
            )

            seriesMap[exercise.uid]?.add(newSeries) ?: run {
                seriesMap[exercise.uid] = mutableListOf(newSeries)
            }
            exerciseAdapter.updateSeries(exercise.uid, seriesMap[exercise.uid] ?: mutableListOf())

            // Zapisz serię w bazie danych
            workoutViewModel.insertSeries(newSeries)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // Funkcja zapisu treningu
    private fun saveWorkout() {
        // Możesz dodać tutaj dowolne dodatkowe akcje związane z zapisywaniem treningu

        // Zakończ aktywność
        finish()
    }
}
