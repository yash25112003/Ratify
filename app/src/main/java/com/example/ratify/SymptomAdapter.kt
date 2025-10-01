package com.example.ratify

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SymptomAdapter(
    private val symptoms: List<Pair<String, Float>>
) : RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder>() {

    val updatedRatings = symptoms.associate { it.first to it.second }.toMutableMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_symptom, parent, false)
        return SymptomViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        val symptomName = symptoms[position].first
        val currentRating = updatedRatings[symptomName] ?: 0.0f
        holder.bind(symptomName, currentRating)
    }

    override fun getItemCount(): Int = symptoms.size

    inner class SymptomViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.symptomNameTextView)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.symptomRatingBar)

        fun bind(symptomName: String, rating: Float) {
            nameTextView.text = symptomName
            ratingBar.rating = rating

            ratingBar.setOnRatingBarChangeListener { _, newRating, _ ->
                updatedRatings[symptomName] = newRating
            }
        }
    }
}