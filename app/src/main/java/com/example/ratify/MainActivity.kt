package com.example.ratify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dao: HealthRecordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dao = AppDatabase.getDatabase(this).healthRecordDao()

        initializeSymptoms()

        val recordDataButton: Button = findViewById(R.id.recordDataButton)
        val deleteAllDataButton: Button = findViewById(R.id.deleteAllDataButton)

        recordDataButton.setOnClickListener {
            val intent = Intent(this, VitalSignsActivity::class.java)
            startActivity(intent)
        }

        deleteAllDataButton.setOnClickListener {
            deleteAllData()
        }
    }

    private fun initializeSymptoms() {
        if (SymptomStore.symptomSeverities.isEmpty()) {
            val symptoms = resources.getStringArray(R.array.symptoms_array)
            for (symptom in symptoms) {
                SymptomStore.symptomSeverities[symptom] = 0.0f
            }
        }
    }

    private fun deleteAllData() {
        lifecycleScope.launch {
            dao.deleteAll()
            Toast.makeText(this@MainActivity, "All recorded data deleted.", Toast.LENGTH_SHORT).show()
        }
    }
}