package com.example.ratify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SymptomActivity : AppCompatActivity() {

    private lateinit var dao: HealthRecordDao
    private lateinit var adapter: SymptomAdapter
    private var heartRate: Float = 0.0f
    private var respiratoryRate: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        dao = AppDatabase.getDatabase(this).healthRecordDao()

        heartRate = intent.getFloatExtra("HEART_RATE", 0.0f)
        respiratoryRate = intent.getFloatExtra("RESPIRATORY_RATE", 0.0f)

        initializeSymptoms()
        setupRecyclerView()

        val submitButton: Button = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            saveHealthRecord()
        }

        Toast.makeText(this, "Vital Signs - HR: ${heartRate.toInt()} BPM, RR: ${respiratoryRate.toInt()} BPM", Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerView() {
        val symptomsRecyclerView: RecyclerView = findViewById(R.id.symptomsRecyclerView)
        val symptomsList = SymptomStore.symptomSeverities.toList()
        adapter = SymptomAdapter(symptomsList)

        symptomsRecyclerView.adapter = adapter
        symptomsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initializeSymptoms() {
        if (SymptomStore.symptomSeverities.isEmpty()) {
            val symptoms = resources.getStringArray(R.array.symptoms_array)
            for (symptom in symptoms) {
                SymptomStore.symptomSeverities[symptom] = 0.0f
            }
        }
    }

    private fun saveHealthRecord() {
        lifecycleScope.launch {
            try {
                val finalRatings = adapter.updatedRatings
                val record = HealthRecord(
                    heartRate = heartRate,
                    respiratoryRate = respiratoryRate,
                    nausea = finalRatings["Nausea"] ?: 0.0f,
                    headache = finalRatings["Headache"] ?: 0.0f,
                    diarrhea = finalRatings["Diarrhea"] ?: 0.0f,
                    soreThroat = finalRatings["Sore Throat"] ?: 0.0f,
                    fever = finalRatings["Fever"] ?: 0.0f,
                    muscleAche = finalRatings["Muscle Ache"] ?: 0.0f,
                    lossOfSmellOrTaste = finalRatings["Loss of Smell or Taste"] ?: 0.0f,
                    cough = finalRatings["Cough"] ?: 0.0f,
                    shortnessOfBreath = finalRatings["Shortness of Breath"] ?: 0.0f,
                    feelingTired = finalRatings["Feeling tired"] ?: 0.0f
                )

                dao.insert(record)
                Toast.makeText(this@SymptomActivity, "Health record saved successfully!", Toast.LENGTH_LONG).show()

                val intent = Intent(this@SymptomActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@SymptomActivity, "Failed to save record: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}