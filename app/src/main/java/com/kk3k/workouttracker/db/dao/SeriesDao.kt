package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {

    // Insert one series
    @Insert
    suspend fun insert(series: Series)

    // Insert multiple series
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg series: Series)

    // Update an existing series
    @Update
    suspend fun update(series: Series)

    // Delete a specific series by object
    @Delete
    suspend fun delete(series: Series)

    // Delete all series
    @Query("DELETE FROM `set`")
    suspend fun deleteAll()

    // Get all series as Flow
    @Query("SELECT * FROM `set`")
    fun getAllSeriesFlow(): Flow<List<Series>>

    // Get all series for a specific workout
    @Query("SELECT * FROM `set` WHERE workout_id = :workoutId")
    fun getSeriesForWorkout(workoutId: Int): Flow<List<Series>>

    // Get a specific series by ID
    @Query("SELECT * FROM `set` WHERE uid = :id LIMIT 1")
    suspend fun getSeriesById(id: Int): Series?

    // Get count of all series
    @Query("SELECT COUNT(*) FROM `set`")
    suspend fun getSeriesCount(): Int

    // Delete all series for a specific workout
    @Query("DELETE FROM `set` WHERE workout_id = :workoutId")
    suspend fun deleteSeriesForWorkout(workoutId: Int)

    // Get series for a specific exercise in a workout
    @Query("SELECT * FROM `set` WHERE workout_id = :workoutId AND exercise_id = :exerciseId")
    fun getSeriesForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<Series>>
}
