package com.kk3k.workouttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDio {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for bodyMeasurement table

    @Insert // Insert record
    fun insertMeasurement(measurement: BodyMeasurement)

    @Delete // Delete record
    fun deleteMeasurement(measurement: BodyMeasurement)

    @Update // Update record
    fun updateMeasurement(measurement: BodyMeasurement)

    @Query("SELECT * FROM bodyMeasurement") // Get all the records
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>

    @Query("DELETE FROM bodyMeasurement") // Clean bodyMeasurement table
    fun dropBodyMeasurement()

    ///////////////////////////////////////////////////////////////////////////////////////////////
}