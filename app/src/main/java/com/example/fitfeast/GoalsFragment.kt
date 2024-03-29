package com.example.fitfeast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GoalsFragment : Fragment(), NutrientInputDialogFragment.NutrientInputListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var goalsAdapter: GoalsAdapter
    private lateinit var mainFab: FloatingActionButton
    private lateinit var weightLossFab: FloatingActionButton
    private lateinit var weightGainFab: FloatingActionButton

    // Text views for displaying nutrition data
    private lateinit var caloriesTextView: TextView
    private lateinit var fatGramsTextView: TextView
    private lateinit var carbsGramsTextView: TextView
    private lateinit var proteinGramsTextView: TextView

    private var isFabMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        // Initialize RecyclerView and Adapter for the goals
        recyclerView = view.findViewById(R.id.goalsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val goalsData = listOf(
            Goal("Water Intake", "Keep track of your hydration."),
            Goal("Weight Management", "Monitor your weight gain or loss.")
        )
        goalsAdapter = GoalsAdapter(goalsData)
        recyclerView.adapter = goalsAdapter

        // Initialize FloatingActionButton
        mainFab = view.findViewById(R.id.mainFab)
        weightLossFab = view.findViewById(R.id.weightLossFab)
        weightGainFab = view.findViewById(R.id.weightGainFab)

        // Initialize TextViews for the Macronutrient information
        caloriesTextView = view.findViewById(R.id.caloriesTextView)
        fatGramsTextView = view.findViewById(R.id.fatGramsTextView)
        carbsGramsTextView = view.findViewById(R.id.carbsGramsTextView)
        proteinGramsTextView = view.findViewById(R.id.proteinGramsTextView)

        // Set OnClickListener for the Macronutrients Card to show the Nutrient Input Dialog
        val macronutrientsCard: CardView = view.findViewById(R.id.macronutrientsCard)
        macronutrientsCard.setOnClickListener {
            showNutrientInputDialog()
        }

        // Fetch and display the latest nutrition data
        fetchLatestNutritionData()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFab.setOnClickListener {
            toggleFabMenu()
        }

        weightLossFab.setOnClickListener {
            // Handle "Weight Loss" action
            Toast.makeText(context, "Weight Loss Clicked", Toast.LENGTH_SHORT).show()
        }

        weightGainFab.setOnClickListener {
            // Handle "Weight Gain" action
            Toast.makeText(context, "Weight Gain Clicked", Toast.LENGTH_SHORT).show()
        }
        //method call to fetch and display nutrition data
                fetchLatestNutritionData()
    }

    override fun onResume() {
        super.onResume()
        fetchLatestNutritionData()
    }

    private fun fetchLatestNutritionData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("nutritionData").orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("NutritionData", "No nutrition data found for user")
                } else {
                    val data = documents.documents.first().toObject(NutritionData::class.java)
                    data?.let {
                        updateNutritionUI(it.calories, it.fatGrams, it.carbsGrams, it.proteinGrams)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("NutritionData", "Error fetching user data", e)
                Toast.makeText(context, "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFabMenu() {
        if (!isFabMenuOpen) {
            weightLossFab.show()
            weightGainFab.show()
            mainFab.setImageResource(R.drawable.ic_delete)
        } else {
            weightLossFab.hide()
            weightGainFab.hide()
            mainFab.setImageResource(R.drawable.ic_add_circle_outline)
        }
        isFabMenuOpen = !isFabMenuOpen
    }

    private fun showNutrientInputDialog() {
        val dialog = NutrientInputDialogFragment()
        dialog.setNutrientInputListener(this)
        dialog.show(parentFragmentManager, "NutrientInputDialogFragment")
    }



    override fun onUpdateNutrientInput(calories: Double, fatGrams: Double, carbsGrams: Double, proteinGrams: Double) {
        // Update UI with the received data
        updateNutritionUI(calories, fatGrams, carbsGrams, proteinGrams)

        // Optionally, confirm with the user before saving the data
        confirmAndSaveNutritionData(calories, fatGrams, carbsGrams, proteinGrams)
    }

    private fun confirmAndSaveNutritionData(calories: Double, fatGrams: Double, carbsGrams: Double, proteinGrams: Double) {
        // (update later) For simplicity, assuming the user always confirms, and directly save the data
        saveNutritionData(calories, fatGrams, carbsGrams, proteinGrams)
    }


    private fun saveNutritionData(calories: Double, fatGrams: Double, carbsGrams: Double, proteinGrams: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val nutritionData = hashMapOf(
                "calories" to calories,
                "fatGrams" to fatGrams,
                "carbsGrams" to carbsGrams,
                "proteinGrams" to proteinGrams,
                "timestamp" to FieldValue.serverTimestamp()
            )

            FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("nutritionData").add(nutritionData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Nutrition data updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating nutrition data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No user logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNutritionUI(calories: Double, fatGrams: Double, carbsGrams: Double, proteinGrams: Double) {
        caloriesTextView?.text = getString(R.string.calories_text, calories)
        fatGramsTextView?.text = getString(R.string.fat_grams_text, fatGrams)
        carbsGramsTextView?.text = getString(R.string.carbs_grams_text, carbsGrams)
        proteinGramsTextView?.text = getString(R.string.protein_grams_text, proteinGrams)
    }
}
