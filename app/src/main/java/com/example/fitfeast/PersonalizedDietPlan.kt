package com.example.fitfeast

class PersonalizedDietPlan(private val userId: String) {
    private var dietPlan: Map<String, String> = emptyMap()

    fun createDietPlan(preferences: Map<String, String>, goals: Map<String, String>) {

        dietPlan = preferences + goals

        println("Diet plan created for user $userId: $dietPlan")
    }

    fun getDietPlan(): Map<String, String> {
        return dietPlan
    }

    fun updateDietPlan(newPlan: Map<String, String>) {
        dietPlan = newPlan
        println("Diet plan updated for user $userId")
    }
}
