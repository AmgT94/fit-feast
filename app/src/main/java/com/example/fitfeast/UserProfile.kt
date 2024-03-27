package com.example.fitfeast

import com.google.firebase.Timestamp


data class UserProfile(
    var name: String = "",
    var dateOfBirth: String = "",
    var gender: String = "",
    var age: Int = 0,
    var weight: Double = 0.0,
    var weightUnit: String = "kg",
    var height: Double = 0.0,
    var heightUnit: String = "cm",
    var activityLevel: String = "",
    var medications: List<Medication> = emptyList(),
    var profileImageUrl: String = ""
)

data class Medication(
    val id: String = "",
    val name: String = "",
    val startDate: String = "",
    val instructions: String = "",
    val pillQuantity: Int = 0,
    val repeatDays: List<String> = emptyList(),
    val notes: String = ""
)

data class Goal(
    val title: String,
    val description: String
)

data class NutritionData(
    val calories: Double = 0.0,
    val fatGrams: Double = 0.0,
    val carbsGrams: Double = 0.0,
    val proteinGrams: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)
