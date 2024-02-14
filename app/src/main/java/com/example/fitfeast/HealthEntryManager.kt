package com.example.fitfeast

import java.text.SimpleDateFormat
import java.util.Locale

data class HealthEntry(val date: String, val type: String, val description: String)

class HealthEntryManager {
    private val entries = mutableListOf<HealthEntry>()

    fun addEntry(entry: HealthEntry) {
        entries.add(entry)
    }

    fun removeEntry(entry: HealthEntry) {
        entries.remove(entry)
    }

    fun removeEntryAt(index: Int) {
        if (index in entries.indices) {
            entries.removeAt(index)
        }
    }

    fun updateEntry(index: Int, newEntry: HealthEntry) {
        if (index in entries.indices) {
            entries[index] = newEntry
        }
    }

    fun getEntries(): List<HealthEntry> {
        return entries.toList()
    }

    fun getEntriesByType(type: String): List<HealthEntry> {
        return entries.filter { it.type == type }
    }

    fun addDietPlanEntry(dietPlan: Map<String, String>, date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())) {
        val description = dietPlan.entries.joinToString(separator = ", ") { "${it.key}: ${it.value}" }
        val dietEntry = HealthEntry(date, "Diet Plan", description)
        addEntry(dietEntry)
    }
}
