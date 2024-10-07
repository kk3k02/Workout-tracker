package com.kk3k.workouttracker

import android.app.Application
import androidx.room.Room
import com.kk3k.workouttracker.db.Database

class Application : Application() {

    companion object {
        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            Database.NAME
        ).build()
    }
}