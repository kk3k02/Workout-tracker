package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Inserts a single workout into the database
    @Insert
    suspend fun insert(workout: Workout)

    // Inserts multiple workouts, replacing any conflicts with new data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg workouts: Workout)

    // Updates an existing workout in the database
    @Update
    suspend fun update(workout: Workout)

    // Deletes a specific workout by object reference
    @Delete
    suspend fun delete(workout: Workout)

    // Deletes all workouts from the database
    @Query("DELETE FROM workout")
    suspend fun deleteAll()

    // Retrieves all workouts as a Flow, emitting updates automatically
    @Query("SELECT * FROM workout")
    fun getAllWorkoutsFlow(): Flow<List<Workout>>

    // Retrieves a specific workout by its ID; returns null if not found
    @Query("SELECT * FROM workout WHERE uid = :id LIMIT 1")
    suspend fun getWorkoutById(id: Int): Workout?

    // Returns the count of all workouts in the database
    @Query("SELECT COUNT(*) FROM workout")
    suspend fun getWorkoutCount(): Int

    // Retrieves the most recent workout based on the date, or null if no workouts exist
    @Query("SELECT * FROM workout ORDER BY date DESC LIMIT 1")
    suspend fun getMostRecentWorkout(): Workout?

    // Checks if a specific workout is marked as finished by its ID
    @Query("SELECT isFinished FROM workout WHERE uid = :id LIMIT 1")
    suspend fun isWorkoutFinished(id: Int): Boolean?

    // Retrieves all finished workouts as a Flow, updating automatically on changes
    @Query("SELECT * FROM workout WHERE isFinished = 1")
    fun getFinishedWorkouts(): Flow<List<Workout>>

    // Retrieves the most recent unfinished workout, or null if all are finished
    @Query("SELECT * FROM workout WHERE isFinished = 0 LIMIT 1")
    suspend fun getUnfinishedWorkout(): Workout?

    // Updates the duration of a specific workout by its ID
    @Query("UPDATE workout SET duration = :duration WHERE uid = :id")
    suspend fun updateDuration(id: Int, duration: Long)

    // Updates the note of a specific workout by its ID
    @Query("UPDATE workout SET notes = :note WHERE uid = :id")
    suspend fun updateNote(id: Int, note: String)

    @Query("SELECT COUNT(*) FROM workout WHERE isFinished = 1")
    suspend fun getFinishedWorkoutCount(): Int

    @Query("SELECT SUM(duration) FROM workout WHERE isFinished = 1")
    suspend fun getTotalWorkoutDuration(): Long?
}