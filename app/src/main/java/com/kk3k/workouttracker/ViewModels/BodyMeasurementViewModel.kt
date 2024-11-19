package com.kk3k.workouttracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BodyMeasurementViewModel(application: Application) : AndroidViewModel(application) {
    private val bodyMeasurementDao = AppDatabase.getDatabase(application).bodyMeasurementDao()

    // Get all body measurements as Flow
    val allMeasurements: Flow<List<BodyMeasurement>> = bodyMeasurementDao.getAllBodyMeasurementsFlow()

    // Insert a new body measurement
    fun insertBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        viewModelScope.launch {
            bodyMeasurementDao.insert(bodyMeasurement)
        }
    }

    // Update an existing body measurement
    fun updateBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        viewModelScope.launch {
            bodyMeasurementDao.update(bodyMeasurement)
        }
    }

    // Delete all body measurements
    fun deleteAllMeasurements() {
        viewModelScope.launch {
            bodyMeasurementDao.deleteAll()
        }
    }

    // Delete a specific body measurement
    fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        viewModelScope.launch {
            bodyMeasurementDao.delete(bodyMeasurement)
        }
    }

    // Get a specific body measurement by ID
    fun getBodyMeasurementById(id: Int, callback: (BodyMeasurement?) -> Unit) {
        viewModelScope.launch {
            val bodyMeasurement = bodyMeasurementDao.getBodyMeasurementById(id)
            callback(bodyMeasurement)
        }
    }

    // Get body measurements by date
    fun getBodyMeasurementsByDate(date: Long): Flow<List<BodyMeasurement>> {
        return bodyMeasurementDao.getBodyMeasurementsByDate(date)
    }

    // Get the most recent body measurement
    fun getMostRecentBodyMeasurement(callback: (BodyMeasurement?) -> Unit) {
        viewModelScope.launch {
            val mostRecentMeasurement = bodyMeasurementDao.getMostRecentBodyMeasurement()
            callback(mostRecentMeasurement)
        }
    }

    // Get the count of all body measurements
    fun getBodyMeasurementCount(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val count = bodyMeasurementDao.getBodyMeasurementCount()
            callback(count)
        }
    }

}
