package com.kk3k.workouttracker.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.ExerciseRepository
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val allExercises: Flow<List<Exercise>> = repository.allExercises

    fun insert(exercise: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(exercise)
    }

    fun update(exercise: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(exercise)
    }

    fun delete(exercise: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(exercise)
    }

    fun dropExercises() = viewModelScope.launch(Dispatchers.IO) {
        repository.dropExercises()
    }
}