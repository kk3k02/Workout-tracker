package com.kk3k.workouttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for 'set' table

    @Insert // Insert record
    suspend fun insert(set: Set)

    @Delete // Delete record
    suspend fun delete(set: Set)

    @Update // Update record
    suspend fun update(set: Set)

    @Query("SELECT * FROM 'set'") // Get all the records
    fun getAllSets(): Flow<List<Set>>

    @Query("DELETE FROM `set`") // Clean 'set' table
    suspend fun dropSets()

    ///////////////////////////////////////////////////////////////////////////////////////////////
}