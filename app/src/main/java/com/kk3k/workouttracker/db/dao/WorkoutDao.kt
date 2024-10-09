package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Insert one workout
    @Insert
    suspend fun insert(workout: Workout)

    // Insert multiple workouts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg workouts: Workout)

    // Update an existing workout
    @Update
    suspend fun update(workout: Workout)

    // Delete a specific workout by object
    @Delete
    suspend fun delete(workout: Workout)

    // Delete all workouts
    @Query("DELETE FROM workout")
    suspend fun deleteAll()

    // Get all workouts as Flow
    @Query("SELECT * FROM workout")
    fun getAllWorkoutsFlow(): Flow<List<Workout>>

    // Get a specific workout by ID
    @Query("SELECT * FROM workout WHERE uid = :id LIMIT 1")
    suspend fun getWorkoutById(id: Int): Workout?

    // Get count of all workouts
    @Query("SELECT COUNT(*) FROM workout")
    suspend fun getWorkoutCount(): Int

    // Get the most recent workout
    @Query("SELECT * FROM workout ORDER BY date DESC LIMIT 1")
    suspend fun getMostRecentWorkout(): Workout?
}
