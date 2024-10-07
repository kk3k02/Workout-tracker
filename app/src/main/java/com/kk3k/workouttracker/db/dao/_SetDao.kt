package com.kk3k.workouttracker.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kk3k.workouttracker.db.entities._Set

@Dao
interface _SetDao {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for 'set' table

    @Insert // Insert record
    suspend fun insert(set: _Set)

    @Delete // Delete record
    suspend fun delete(set: _Set)

    @Update // Update record
    suspend fun update(set: _Set)

    @Query("SELECT * FROM 'set'") // Get all the records
    fun getAllSets(): LiveData<List<_Set>>

    @Query("DELETE FROM `set`") // Clean 'set' table
    suspend fun dropSets(): Int

    ///////////////////////////////////////////////////////////////////////////////////////////////
}