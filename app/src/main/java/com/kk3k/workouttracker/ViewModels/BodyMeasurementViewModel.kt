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

    fun insertSampleBodyMeasurements(bodyMeasurementViewModel: BodyMeasurementViewModel) {
        for (i in 1..10) {
            val bodyMeasurement = BodyMeasurement(
                date = System.currentTimeMillis() - (i * 1000L * 60L * 60L * 24L),
                weight = 70 + i,
                biceps = 30 + (i % 5),
                triceps = 30 + (i % 5),
                chest = 100 + (i % 10),
                waist = 80 + (i % 10),
                hips = 90 + (i % 10),
                thighs = 60 + (i % 5),
                calves = 40 + (i % 5),
                notes = "Body measurement number $i"
            )
            bodyMeasurementViewModel.insertBodyMeasurement(bodyMeasurement)
        }
    }

}
