package com.kk3k.workouttracker.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kk3k.workouttracker.db.AppDatabase
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ViewModel class for managing body measurement data
class BodyMeasurementViewModel(application: Application) : AndroidViewModel(application) {

    // Access the BodyMeasurementDao to interact with the body_measurement table in the database
    private val bodyMeasurementDao = AppDatabase.getDatabase(application).bodyMeasurementDao()

    // Flow to observe all body measurements in the database
    val allMeasurements: Flow<List<BodyMeasurement>> = bodyMeasurementDao.getAllBodyMeasurementsFlow()

    // Function to insert a new body measurement into the database
    fun insertBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        viewModelScope.launch {
            // Launch a coroutine to insert the body measurement in the background
            bodyMeasurementDao.insert(bodyMeasurement)
        }
    }

    // Function to delete all body measurements from the database
    fun deleteAllMeasurements() {
        viewModelScope.launch {
            // Launch a coroutine to delete all body measurements in the background
            bodyMeasurementDao.deleteAll()
        }
    }

    // Function to delete a specific body measurement from the database
    fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurement) {
        viewModelScope.launch {
            // Launch a coroutine to delete the specific body measurement in the background
            bodyMeasurementDao.delete(bodyMeasurement)
        }
    }
}