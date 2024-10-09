package com.kk3k.workouttracker

import android.app.Application
import com.kk3k.workouttracker.db.AppDatabase

class WorkoutTrackerApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
