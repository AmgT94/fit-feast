package com.example.fitfeast

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitfeast.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var healthEntryManager: HealthEntryManager
    // Declare personalizedDietPlan here
    private lateinit var personalizedDietPlan: PersonalizedDietPlan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize HealthEntryManager
        healthEntryManager = HealthEntryManager()

        // Initialize PersonalizedDietPlan
        personalizedDietPlan = PersonalizedDietPlan("user123")

        // Setup RecyclerView and buttons
        initializeRecyclerView()
        setupButtons()
    }

    private fun initializeRecyclerView() {
        val entries = healthEntryManager.getEntries().toMutableList()
        val entriesAdapter = EntriesAdapter(entries)
        with(binding.entriesRecyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = entriesAdapter
        }
    }

    private fun setupButtons() {
        binding.dietPlanButton.setOnClickListener {
            showAddDietPlanDialog()
        }

        binding.dailyReportButton.setOnClickListener {
            showToast("Daily report generated")
        }

        binding.weeklyReportButton.setOnClickListener {
            showToast("Weekly report generated")
        }

        binding.addEntryButton.setOnClickListener {
            val newEntry = HealthEntry(
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date()),
                type = "Exercise",
                description = "Jogged 3 miles"
            )
            healthEntryManager.addEntry(newEntry)
            showToast("Health Entry Added")
            displayEntries()
        }

        binding.viewEntriesButton.setOnClickListener {
            displayEntries()
        }
    }

    private fun showAddDietPlanDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_diet_plan)

        val dietPlanSpinner: Spinner = dialog.findViewById(R.id.dietPlanSpinner)
        val mealFrequencySpinner: Spinner = dialog.findViewById(R.id.mealFrequencySpinner)
        val glutenFreeCheckBox: CheckBox = dialog.findViewById(R.id.glutenFreeCheckBox)
        val dairyFreeCheckBox: CheckBox = dialog.findViewById(R.id.dairyFreeCheckBox)
        val startDateEditText: EditText = dialog.findViewById(R.id.startDateEditText)
        val goalRadioGroup: RadioGroup = dialog.findViewById(R.id.goalRadioGroup)
        val addButton: Button = dialog.findViewById(R.id.addDietPlanButton)

        // Setup Spinner options for Diet Plan
        val dietPlans = arrayOf("Vegan", "Ketogenic", "Mediterranean", "Vegetarian")
        dietPlanSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dietPlans)

        // Setup Spinner options for Meal Frequency
        val mealFrequencies = arrayOf("3 meals/day", "4 meals/day", "5 meals/day", "6 meals/day")
        mealFrequencySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealFrequencies)

        addButton.setOnClickListener {
            val selectedDietPlan = dietPlanSpinner.selectedItem.toString()
            val selectedMealFrequency = mealFrequencySpinner.selectedItem.toString()
            val glutenFree = glutenFreeCheckBox.isChecked
            val dairyFree = dairyFreeCheckBox.isChecked
            val startDate = startDateEditText.text.toString()
            val selectedGoalId = goalRadioGroup.checkedRadioButtonId
            val selectedGoal = dialog.findViewById<RadioButton>(selectedGoalId).text.toString()

            val description = buildString {
                append("Plan: $selectedDietPlan, Goal: $selectedGoal, ")
                append("Meal Frequency: $selectedMealFrequency, ")
                append("Gluten-Free: $glutenFree, Dairy-Free: $dairyFree, ")
                append("Start Date: $startDate")
            }

            val newEntry = HealthEntry(
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), // Consider using startDate if it should reflect the entry date
                type = "Diet Plan",
                description = description
            )
            healthEntryManager.addEntry(newEntry)
            dialog.dismiss()
        }
        dialog.window?.apply {
            val width = (context.resources.displayMetrics.widthPixels * 0.95).toInt() // Set dialog width to 95% of screen width
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        dialog.show()
    }



    private fun displayEntries() {
        val newEntries = healthEntryManager.getEntries()
        (binding.entriesRecyclerView.adapter as? EntriesAdapter)?.updateEntries(newEntries)
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
