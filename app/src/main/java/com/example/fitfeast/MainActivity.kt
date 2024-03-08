package com.example.fitfeast

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.fitfeast.databinding.ActivityMainBinding
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.tabs.TabLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        // Set up the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupTabLayout()

        setupDrawer()

        // Listen for navigation changes to update UI components accordingly
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment -> {
                    setDrawerEnabled(true)
                    setTabsVisibility(false)
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.dashboard)
                }
                R.id.userProfileCreationFragment -> {
                    setDrawerEnabled(false)
                    setTabsVisibility(false)
                    supportActionBar?.hide()
                }
                R.id.profileFragment -> {
                    setDrawerEnabled(true)
                    setTabsVisibility(false)
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.profile)
                }
                R.id.medicationFragment -> {
                    setDrawerEnabled(true)
                    setTabsVisibility(false)
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.medication)
                }
                R.id.signInFragment, R.id.signUpFragment -> {
                    setDrawerEnabled(false)
                    setTabsVisibility(true)
                    supportActionBar?.hide()
                }
                else -> {
                    setDrawerEnabled(false)
                    setTabsVisibility(false)
                    supportActionBar?.show()
                    supportActionBar?.title = getString(R.string.app_name)
                }
            }
        }



    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbarMain,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set up the NavigationView listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_goals -> {
                }
                R.id.nav_weight -> {
                }
                R.id.nav_medication -> navController.navigate(R.id.medicationFragment)
                // ... add other menu items here
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Find the header view and the edit button within it
        val headerView = binding.navView.getHeaderView(0)
        val editProfileButton = headerView.findViewById<ImageButton>(R.id.editProfileButton)

        // Set up the click listener for the edit button
        editProfileButton.setOnClickListener {
            navController.navigate(R.id.profileFragment)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }


    private fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
        drawerToggle.isDrawerIndicatorEnabled = enabled
    }

    private fun setTabsVisibility(visible: Boolean) {
        binding.tabLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setupTabLayout() {
        binding.tabLayout.apply {
            addTab(newTab().setText("Sign In"))
            addTab(newTab().setText("Sign Up"))

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
