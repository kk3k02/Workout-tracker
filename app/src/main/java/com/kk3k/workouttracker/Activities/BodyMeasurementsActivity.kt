package com.kk3k.workouttracker.Activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk3k.workouttracker.Adapters.BodyMeasurementAdapter
import com.kk3k.workouttracker.R
import com.kk3k.workouttracker.viewmodel.BodyMeasurementViewModel
import kotlinx.coroutines.launch

class BodyMeasurementActivity : AppCompatActivity() {

    private val bodyMeasurementViewModel: BodyMeasurementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBodyMeasurements)
        val adapter = BodyMeasurementAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Zbieranie danych z ViewModel za pomocą Flow
        lifecycleScope.launch {
            bodyMeasurementViewModel.allMeasurements.collect { measurements ->
                adapter.submitList(measurements)
            }
        }
    }
}
