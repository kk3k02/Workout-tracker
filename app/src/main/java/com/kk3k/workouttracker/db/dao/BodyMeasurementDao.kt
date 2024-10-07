package com.kk3k.workouttracker.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kk3k.workouttracker.db.entities.BodyMeasurement

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

    @Query("SELECT * FROM body_measurement") // Get all the records
    fun getAllMeasurements(): LiveData<List<BodyMeasurement>>

//    @Query("DELETE FROM bodyMeasurement") // Clean bodyMeasurement table
//    suspend fun dropMeasurements(): Int

    ///////////////////////////////////////////////////////////////////////////////////////////////
}