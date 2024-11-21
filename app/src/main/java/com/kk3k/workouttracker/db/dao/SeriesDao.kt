package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {

    // Insert a single series into the database
    @Insert
    suspend fun insert(series: Series)

    // Insert multiple series into the database. If any series already exist, they will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg series: Series)

    // Update an existing series in the database
    @Update
    suspend fun update(series: Series)

    // Delete a specific series from the database
    @Delete
    suspend fun delete(series: Series)

    // Delete all series from the database
    @Query("DELETE FROM series")
    suspend fun deleteAll()

    // Get all series from the database as a Flow (for live updates)
    @Query("SELECT * FROM series")
    fun getAllSeriesFlow(): Flow<List<Series>>

    // Get all series related to a specific workout, identified by workoutId, as a Flow
    @Query("SELECT * FROM series WHERE workout_id = :workoutId")
    fun getSeriesForWorkout(workoutId: Int): Flow<List<Series>>

    // Get a specific series by its ID
    @Query("SELECT * FROM series WHERE uid = :id LIMIT 1")
    suspend fun getSeriesById(id: Int): Series?

    // Get the total count of all series in the database
    @Query("SELECT COUNT(*) FROM series")
    suspend fun getSeriesCount(): Int

    // Delete all series for a specific workout, identified by workoutId
    @Query("DELETE FROM series WHERE workout_id = :workoutId")
    suspend fun deleteSeriesForWorkout(workoutId: Int)

    // Get all series for a specific exercise in a specific workout, identified by workoutId and exerciseId
    @Query("SELECT * FROM series WHERE workout_id = :workoutId AND exercise_id = :exerciseId")
    fun getSeriesForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<Series>>

    // Get the distinct list of exercise IDs for a specific workout
    @Query("SELECT DISTINCT exercise_id FROM series WHERE workout_id = :workoutId")
    suspend fun getExerciseIdsForWorkout(workoutId: Int): List<Int>

    // Get all series for a specific workout and exercise, identified by workoutId and exerciseId
    @Query("SELECT * FROM series WHERE workout_id = :workoutId AND exercise_id = :exerciseId")
    suspend fun getSeriesForWorkoutAndExercise(workoutId: Int, exerciseId: Int): List<Series>

    // Get all series for a specific exercise, identified by exerciseId
    @Query("SELECT * FROM series WHERE exercise_id = :exerciseId")
    suspend fun getSeriesForExercise(exerciseId: Int): List<Series>

    // Get the total weight used in all series where weight and repetitions are available
    @Query("SELECT SUM(weight * repetitions) FROM series WHERE weight IS NOT NULL AND repetitions > 0")
    suspend fun getTotalWeightUsed(): Float?
}
