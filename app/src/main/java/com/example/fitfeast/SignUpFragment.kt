package com.example.fitfeast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitfeast.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailSignUp.text.toString().trim()
            val password = binding.editTextPasswordSignUp.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (password == confirmPassword && email.isNotEmpty() && password.isNotEmpty()) {
                createAccount(email, password)
            } else {
                Toast.makeText(context, "Passwords do not match or field is empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Navigate to DashboardFragment upon successful sign-up
                    findNavController().navigate(R.id.action_signUpFragment_to_dashboardFragment)
                } else {
                    // Handle errors
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthWeakPasswordException -> Toast.makeText(context, "Password is too weak.", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthUserCollisionException -> Toast.makeText(context, "An account with this email already exists.", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
