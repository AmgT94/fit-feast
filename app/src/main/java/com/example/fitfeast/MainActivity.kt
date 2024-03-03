package com.example.fitfeast

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fitfeast.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Listen for navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.userProfileCreationFragment -> {
                    // Hide the tabs
                    binding.tabLayout.visibility = View.GONE
                }
                R.id.dashboardFragment -> {
                    binding.tabLayout.visibility = View.GONE
                }
                else -> {
                    binding.tabLayout.visibility = View.VISIBLE
                }
            }
        }


        setupTabLayout()
    }

    private fun setupTabLayout() {
        val tabTitles = arrayOf("Sign In", "Sign Up")

        binding.tabLayout.apply {
            addTab(newTab().setText(tabTitles[0]))
            addTab(newTab().setText(tabTitles[1]))
        }

        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> navController.navigate(R.id.signInFragment)
                    1 -> navController.navigate(R.id.signUpFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
