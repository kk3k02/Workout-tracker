package com.kk3k.workouttracker

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kk3k.workouttracker.databinding.ActivityFigureBinding

class FigureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFigureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFigureBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}