package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {

    // Insert one body measurement
    @Insert
    suspend fun insert(bodyMeasurement: BodyMeasurement)

    // Insert multiple body measurements
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg bodyMeasurements: BodyMeasurement)

    // Update an existing body measurement
    @Update
    suspend fun update(bodyMeasurement: BodyMeasurement)

    // Delete a specific body measurement by object
    @Delete
    suspend fun delete(bodyMeasurement: BodyMeasurement)

    // Delete all body measurements
    @Query("DELETE FROM body_measurement")
    suspend fun deleteAll()

    // Get all body measurements as Flow
    @Query("SELECT * FROM body_measurement ORDER BY date ASC")
    fun getAllBodyMeasurementsFlow(): Flow<List<BodyMeasurement>>

    // Get a specific body measurement by ID
    @Query("SELECT * FROM body_measurement WHERE uid = :id LIMIT 1")
    suspend fun getBodyMeasurementById(id: Int): BodyMeasurement?

    // Get body measurements by date
    @Query("SELECT * FROM body_measurement WHERE date = :date")
    fun getBodyMeasurementsByDate(date: Long): Flow<List<BodyMeasurement>>

    // Get the most recent body measurement
    @Query("SELECT * FROM body_measurement ORDER BY date DESC LIMIT 1")
    suspend fun getMostRecentBodyMeasurement(): BodyMeasurement?

    // Get count of all body measurements
    @Query("SELECT COUNT(*) FROM body_measurement")
    suspend fun getBodyMeasurementCount(): Int
}
