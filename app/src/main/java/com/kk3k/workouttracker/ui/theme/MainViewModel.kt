package com.kk3k.workouttracker.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(app.applicationContext)
}