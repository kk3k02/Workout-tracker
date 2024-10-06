package com.kk3k.workouttracker.ui.theme

import android.content.Context
import com.kk3k.workouttracker.data.BodyMeasurement
import com.kk3k.workouttracker.data.BodyMeasurementDao
import com.kk3k.workouttracker.data.Db
import com.kk3k.workouttracker.data.Exercise
import com.kk3k.workouttracker.data.ExerciseDao
import com.kk3k.workouttracker.data.Set
import com.kk3k.workouttracker.data.SetDao
import com.kk3k.workouttracker.data.Workout
import com.kk3k.workouttracker.data.WorkoutDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(context: Context): BodyMeasurementDao, WorkoutDao, SetDao, ExerciseDao {

    // Creating instances of the DAOs from the database singleton instance.
    private val measurementDao = Db.getInstance(context).bodyMeasurementDao()
    private val workoutDao = Db.getInstance(context).workoutDao()
    private val setDao = Db.getInstance(context).setDao()
    private val exerciseDao = Db.getInstance(context).exerciseDao()

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Implementing methods from BodyMeasurementDao

    // Insert a BodyMeasurement record into the database
    override suspend fun insert(measurement: BodyMeasurement) = withContext(Dispatchers.IO) {
        measurementDao.insert(measurement)
    }

    // Delete a BodyMeasurement record from the database
    override suspend fun delete(measurement: BodyMeasurement) = withContext(Dispatchers.IO) {
        measurementDao.delete(measurement)
    }

    // Update a BodyMeasurement record in the database
    override suspend fun update(measurement: BodyMeasurement) = withContext(Dispatchers.IO) {
        measurementDao.update(measurement)
    }

    // Return all BodyMeasurement records as a Flow
    override fun getAllMeasurements(): Flow<List<BodyMeasurement>>  {
        return measurementDao.getAllMeasurements()
    }

    // Drop the BodyMeasurement table
    override suspend fun dropMeasurements() = withContext(Dispatchers.IO) {
        measurementDao.dropMeasurements()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Implementing methods from WorkoutDao

    // Insert a Workout record into the database
    override suspend fun insert(workout: Workout) = withContext(Dispatchers.IO) {
        workoutDao.insert(workout)
    }

    // Delete a Workout record from the database
    override suspend fun delete(workout: Workout) = withContext(Dispatchers.IO) {
        workoutDao.delete(workout)
    }

    // Update a Workout record in the database
    override suspend fun update(workout: Workout) = withContext(Dispatchers.IO) {
        workoutDao.update(workout)
    }

    // Return all Workout records as a Flow
    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts()
    }

    // Drop the Workout table
    override suspend fun dropWorkouts() = withContext(Dispatchers.IO) {
        workoutDao.dropWorkouts()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Implementing methods from SetDao

    // Insert a Set record into the database
    override suspend fun insert(set: Set) = withContext(Dispatchers.IO) {
        setDao.insert(set)
    }

    // Delete a Set record from the database
    override suspend fun delete(set: Set) = withContext(Dispatchers.IO) {
        setDao.delete(set)
    }

    // Update a Set record in the database
    override suspend fun update(set: Set) = withContext(Dispatchers.IO) {
        setDao.update(set)
    }

    // Return all Set records as a Flow
    override fun getAllSets(): Flow<List<Set>> {
        return setDao.getAllSets()
    }

    // Drop the Set table
    override suspend fun dropSets() = withContext(Dispatchers.IO) {
        setDao.dropSets()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Implementing methods from ExerciseDao

    // Insert an Exercise record into the database
    override suspend fun insert(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.insert(exercise)
    }

    // Delete an Exercise record from the database
    override suspend fun delete(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.delete(exercise)
    }

    // Update an Exercise record in the database
    override suspend fun update(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.update(exercise)
    }

    // Return all Exercise records as a Flow
    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises()
    }

    // Drop the Exercise table
    override suspend fun dropExercises() = withContext(Dispatchers.IO) {
        exerciseDao.dropExercises()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
}
