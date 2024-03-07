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

        // Set up the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupTabLayout()

        // Listen for navigation changes to show or hide the TabLayout accordingly
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.userProfileCreationFragment, R.id.dashboardFragment -> {
                    binding.tabLayout.visibility = View.GONE
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.dashboard)
                }
                R.id.profileFragment -> {
                    binding.tabLayout.visibility = View.GONE
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.profile)
                }
                else -> {
                    binding.tabLayout.visibility = View.VISIBLE
                    supportActionBar?.title = getString(R.string.app_name)
                }
            }
        }
    }

    private fun setupTabLayout() {
        val tabTitles = arrayOf("Sign In", "Sign Up")

        binding.tabLayout.apply {
            removeAllTabs()
            addTab(newTab().setText(tabTitles[0]))
            addTab(newTab().setText(tabTitles[1]))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> navController.navigate(R.id.signInFragment)
                        1 -> navController.navigate(R.id.signUpFragment)
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }
}

