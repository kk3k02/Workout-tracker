package com.kk3k.workouttracker.db.dao

import androidx.room.*
import com.kk3k.workouttracker.db.entities.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    // Insert one exercise
    @Insert
    suspend fun insert(exercise: Exercise)

    // Insert multiple exercises
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg exercises: Exercise)

    // Update an existing exercise
    @Update
    suspend fun update(exercise: Exercise)

    // Delete a specific exercise by object
    @Delete
    suspend fun delete(exercise: Exercise)

    // Delete all exercises
    @Query("DELETE FROM exercise")
    suspend fun deleteAll()

    // Get all exercises as Flow
    @Query("SELECT * FROM exercise")
    fun getAllExercisesFlow(): Flow<List<Exercise>>

    // Get a specific exercise by ID
    @Query("SELECT * FROM exercise WHERE uid = :id LIMIT 1")
    suspend fun getExerciseById(id: Int): Exercise?

    // Get count of all exercises
    @Query("SELECT COUNT(*) FROM exercise")
    suspend fun getExerciseCount(): Int

    // Get exercises by target muscle group
    @Query("SELECT * FROM exercise WHERE target_muscle = :targetMuscle")
    fun getExercisesByTargetMuscle(targetMuscle: String): Flow<List<Exercise>>

    // Get exercises by name (case insensitive)
    @Query("SELECT * FROM exercise WHERE LOWER(name) LIKE LOWER(:name)")
    fun getExercisesByName(name: String): Flow<List<Exercise>>
}
