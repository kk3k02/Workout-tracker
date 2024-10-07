package com.kk3k.workouttracker.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker._SetRepository
import com.kk3k.workouttracker.db.entities._Set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class _SetViewModel(private val repository: _SetRepository) : ViewModel() {
    val allSets: Flow<List<_Set>> = repository.allSets

    fun insert(set: _Set) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(set)
    }

    fun update(set: _Set) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(set)
    }

    fun delete(set: _Set) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(set)
    }

    fun dropSets() = viewModelScope.launch(Dispatchers.IO) {
        repository.dropSets()
    }
}