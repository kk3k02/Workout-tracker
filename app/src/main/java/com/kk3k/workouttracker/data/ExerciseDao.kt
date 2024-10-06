package com.kk3k.workouttracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Interface for Exercise table

    @Insert // Insert record
    suspend fun insert(exercise: Exercise)

    @Delete // Delete record
    suspend fun delete(exercise: Exercise)

    @Update // Update record
    suspend fun update(exercise: Exercise)

    @Query("SELECT * FROM exercise") // Get all the records
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("DELETE FROM exercise") // Clean Exercise table
    suspend fun dropExercises()

    ///////////////////////////////////////////////////////////////////////////////////////////////
}