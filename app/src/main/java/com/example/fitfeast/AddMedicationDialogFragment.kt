package com.example.fitfeast

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.fitfeast.databinding.DialogAddMedicationBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AddMedicationDialogFragment : DialogFragment() {

    interface MedicationUpdateListener {
        fun onMedicationUpdated()
    }

    private var _binding: DialogAddMedicationBinding? = null
    private val binding get() = _binding!!
    private var medicationId: String? = null

    var updateListener: MedicationUpdateListener? = null

    companion object {
        const val MEDICATION_ID = "medication_id"

        fun newInstance(medicationId: String?): AddMedicationDialogFragment {
            val fragment = AddMedicationDialogFragment()
            val args = Bundle()
            args.putString(MEDICATION_ID, medicationId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicationId = it.getString(MEDICATION_ID)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddMedicationBinding.inflate(LayoutInflater.from(context))

        setupInstructionsDropdown()


        binding.startDateEditText.setOnClickListener {
            showDatePickerDialog()
        }


        // Initialize dialog
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (medicationId == null) "Add Medication" else "Edit Medication")
            .setView(binding.root)
            .setPositiveButton(if (medicationId == null) "Add" else "Update") { dialog, which ->
                attemptToSaveMedication()
            }
            .setNegativeButton("Cancel", null)

        // If in edit mode, fetch and populate the data
        medicationId?.let {
            dialogBuilder.setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmationDialog(it)
            }
        }

        return dialogBuilder.create()
    }

    private fun showDeleteConfirmationDialog(medicationId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Medication")
            .setMessage("Are you sure you want to delete this medication?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMedication(medicationId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMedication(medicationId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("medications").document(medicationId).delete()
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(context, "Medication deleted successfully", Toast.LENGTH_SHORT).show()
                    updateListener?.onMedicationUpdated()
                    dismiss()
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(context, "Error deleting medication: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun validateChipSelection(): Boolean {
        val chipGroup = binding.daysChipGroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.isChecked == true) {
                return true
            }
        }
        return false
    }
    private fun getSelectedDays(): List<String> {
        val selectedDays = mutableListOf<String>()
        val chipGroup = binding.daysChipGroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.isChecked == true) {
                selectedDays.add(chip.text.toString())
            }
        }
        return selectedDays
    }

    private fun setupInstructionsDropdown() {
        val instructionsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.instructions_options,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.instructionsSpinner.setAdapter(instructionsAdapter)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Create a calendar instance with the selected date
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

            // Format the date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = dateFormat.format(selectedCalendar.time)

            // Set the formatted date to the startDateEditText
            binding.startDateEditText.setText(selectedDate)
        }, year, month, day).apply {
            show()
        }
    }



    private fun fetchAndPopulateMedicationData(medicationId: String) {
        // Fetch medication data from Firestore and populate the form fields
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .collection("medications").document(medicationId).get()
            .addOnSuccessListener { documentSnapshot ->
                val medication = documentSnapshot.toObject(Medication::class.java)
                medication?.let {
                    binding.medicationNameEditText.setText(it.name)
                    binding.startDateEditText.setText(it.startDate)
                    binding.instructionsSpinner.setText(it.instructions, false)
                    binding.pillQuantityEditText.setText(it.pillQuantity.toString())
                    binding.notesEditText.setText(it.notes)
                    // Handle repeatDays chips selection
                    it.repeatDays.forEach { day ->
                        val chip = binding.daysChipGroup.findViewWithTag<Chip>(day)
                        chip?.isChecked = true // Ensure chip is not null before calling setChecked
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FetchMedication", "Error fetching medication details", e)
            }
    }


    private fun attemptToSaveMedication() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "User not identified", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate chip selection
        if (!validateChipSelection()) {
            Toast.makeText(context, "Please select at least one day.", Toast.LENGTH_SHORT).show()
            return
        }

        // Extract values directly from the form
        val name = binding.medicationNameEditText.text.toString().trim()
        val startDate = binding.startDateEditText.text.toString().trim()
        val instructions = binding.instructionsSpinner.text.toString()
        val pillQuantityText = binding.pillQuantityEditText.text.toString()
        val notes = binding.notesEditText.text.toString().trim()

        // Basic validation
        if (name.isEmpty() || startDate.isEmpty() || pillQuantityText.isEmpty()) {
            Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val pillQuantity = pillQuantityText.toIntOrNull()
        if (pillQuantity == null || pillQuantity <= 0) {
            Toast.makeText(context, "Pill quantity must be a positive number.", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve selected days using the getSelectedDays() method
        val selectedDays = getSelectedDays()

        val medication = Medication(
            id = medicationId ?: "",
            name = name,
            startDate = startDate,
            instructions = instructions,
            pillQuantity = pillQuantity ?: 0, // Ensure there's a fallback value
            repeatDays = selectedDays,
            notes = notes
        )

        // Determine whether to add a new medication or update an existing one
        if (medicationId == null) {
            addMedicationToFirestore(userId, medication)
        } else {
            updateMedicationInFirestore(userId, medicationId ?: "", medication)
        }


    }




    private fun addMedicationToFirestore(userId: String, medication: Medication) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("medications").add(medication)
            .addOnSuccessListener {
                if(isAdded) { // Check if the fragment is currently added to its activity
                    Toast.makeText(context, "Medication added successfully.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
            .addOnFailureListener { e ->
                if(isAdded) {
                    Toast.makeText(context, "Failed to add medication: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateMedicationInFirestore(userId: String, medicationId: String, medication: Medication) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("medications").document(medicationId)
            .set(medication)
            .addOnSuccessListener {
                updateListener?.onMedicationUpdated()
                if(isAdded) {
                    Toast.makeText(context, "Medication updated successfully.", Toast.LENGTH_SHORT).show()
                    dismiss() // Close the dialog
                }
            }
            .addOnFailureListener { e ->
                if(isAdded) {
                    Toast.makeText(context, "Failed to update medication: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}