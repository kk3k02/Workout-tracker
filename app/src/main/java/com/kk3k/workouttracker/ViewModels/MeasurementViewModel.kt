package com.kk3k.workouttracker.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.MeasurementRepository
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MeasurementViewModel(private val repository: MeasurementRepository) : ViewModel() {
    val allMeasurements: Flow<List<BodyMeasurement>> = repository.allMeasurements

    fun insert(measurement: BodyMeasurement) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(measurement)
    }

    fun update(measurement: BodyMeasurement) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(measurement)
    }

    fun delete(measurement: BodyMeasurement) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(measurement)
    }

    fun dropMeasurements() = viewModelScope.launch(Dispatchers.IO) {
        repository.dropMeasurements()
    }
}