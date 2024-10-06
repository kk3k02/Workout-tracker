package com.kk3k.workouttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for bodyMeasurement table

    @Insert // Insert record
    suspend fun insert(measurement: BodyMeasurement)

    @Delete // Delete record
    suspend fun delete(measurement: BodyMeasurement)

    @Update // Update record
    suspend fun update(measurement: BodyMeasurement)

    @Query("SELECT * FROM bodyMeasurement") // Get all the records
    fun getAll(): Flow<List<BodyMeasurement>>

    @Query("DELETE FROM bodyMeasurement") // Clean bodyMeasurement table
    suspend fun drop()

    ///////////////////////////////////////////////////////////////////////////////////////////////
}