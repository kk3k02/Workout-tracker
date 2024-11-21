package com.kk3k.workouttracker

import android.app.Application
import com.kk3k.workouttracker.db.AppDatabase

// Custom Application class to initialize and provide global access to the database
class WorkoutTrackerApp : Application() {

    // Lazy initialization of the database instance
    // The database will only be created when it's accessed for the first time
    val database by lazy { AppDatabase.getDatabase(this) }

    // onCreate is called when the application is created
    // Here, it is used to initialize any necessary components (though we don't need additional setup in this case)
    override fun onCreate() {
        super.onCreate()
        // You can initialize other global app settings or dependencies here if needed
    }
}
