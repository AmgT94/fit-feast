package com.example.fitfeast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitfeast.databinding.FragmentWeightManagementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WeightManagementFragment : Fragment() {

    private var _binding: FragmentWeightManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeightManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddWeight.setOnClickListener {
            findNavController().navigate(WeightManagementFragmentDirections.actionWeightManagementFragmentToFragmentAddWeight())
        }

        fetchWeightMetrics()
    }

    private fun fetchWeightMetrics() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId).collection("weights")
            .get()
            .addOnSuccessListener { documents ->
                val weights = documents.mapNotNull { it.getDouble("weight") }
                if (weights.isNotEmpty()) {
                    val minWeight = weights.minOrNull()
                    val maxWeight = weights.maxOrNull()
                    val avgWeight = weights.average()

                    binding.textViewMinWeightValue.text = String.format("%.2f", minWeight)
                    binding.textViewMaxWeightValue.text = String.format("%.2f", maxWeight)
                    binding.textViewAvgWeightValue.text = String.format("%.2f", avgWeight)
                }
            }
            .addOnFailureListener { e ->
                Log.e("WeightManagement", "Error fetching weight metrics", e)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
