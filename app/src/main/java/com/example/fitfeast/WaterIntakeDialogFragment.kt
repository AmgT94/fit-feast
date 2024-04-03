package com.example.fitfeast

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class WaterIntakeDialogFragment : DialogFragment() {
    interface WaterIntakeDialogListener {
        fun onWaterIntakeEntered(waterIntake: Double)
    }

    var listener: WaterIntakeDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_water_intake, null)

        val editTextWaterIntake: EditText = view.findViewById(R.id.editTextWaterIntake)
        val textViewLiter: TextView = view.findViewById(R.id.textViewLiter)

        builder.setView(view)
            .setTitle("Enter Water Intake")
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                val waterIntakeStr = editTextWaterIntake.text.toString()
                if (waterIntakeStr.isNotEmpty()) {
                    val waterIntake = waterIntakeStr.toDoubleOrNull()
                    if (waterIntake != null) {
                        listener?.onWaterIntakeEntered(waterIntake)
                    } else {
                        Toast.makeText(context, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter your water intake.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, id ->
                dialog.cancel()
            }

        return builder.create()
    }

    companion object {
        fun newInstance(listener: WaterIntakeDialogListener): WaterIntakeDialogFragment {
            val fragment = WaterIntakeDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }
}

