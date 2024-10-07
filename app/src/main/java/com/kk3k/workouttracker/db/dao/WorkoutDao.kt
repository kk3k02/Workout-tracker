package com.kk3k.workouttracker.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for workout table

    @Insert // Insert record
    suspend fun insert(workout: Workout)

    @Delete // Delete record
    suspend fun delete(workout: Workout)

    @Update // Update record
    suspend fun update(workout: Workout)

    @Query("SELECT * FROM workout") // Get all the records
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("DELETE FROM workout") // Clean workout table
    suspend fun dropWorkouts(): Int

    ///////////////////////////////////////////////////////////////////////////////////////////////
}