package com.kk3k.workouttracker

import android.app.Application
import androidx.room.Room
import com.kk3k.workouttracker.db.AppDatabase

class MainApplication : Application() {

    companion object {
        lateinit var appDatabase: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).build()
    }
}