package com.example.fitfeast

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbarMain)

        // Set up the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupTabLayout()
        setupDrawer()

        // Fetch and set the user's name in the navigation drawer header
        setupNavigationHeader()

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
    private fun setupNavigationHeader() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                    val headerView: View = binding.navView.getHeaderView(0)
                    val imageViewHeader: ImageView = headerView.findViewById(R.id.imageViewHeader)
                    val textViewHeaderName: TextView = headerView.findViewById(R.id.textViewHeaderName)
                    textViewHeaderName.text = userProfile?.name ?: "Guest"

                    // Load the profile image as a circle
                    Glide.with(this)
                        .load(userProfile?.profileImageUrl)
                        .placeholder(R.drawable.ic_add_a_photo)
                        .circleCrop()
                        .into(imageViewHeader)
                }
                .addOnFailureListener { e ->
                    // Handle the failure
                    val headerView: View = binding.navView.getHeaderView(0)
                    val imageViewHeader: ImageView = headerView.findViewById(R.id.imageViewHeader)
                    Glide.with(this)
                        .load(R.drawable.ic_add_a_photo)
                        .circleCrop()
                        .into(imageViewHeader)
                }
        } else {
            // User is not signed in; set a default name and image
            val headerView: View = binding.navView.getHeaderView(0)
            val imageViewHeader: ImageView = headerView.findViewById(R.id.imageViewHeader)
            val textViewHeaderName: TextView = headerView.findViewById(R.id.textViewHeaderName)
            textViewHeaderName.text = "Guest"
            Glide.with(this)
                .load(R.drawable.ic_add_a_photo)
                .circleCrop()
                .into(imageViewHeader)
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
                R.id.nav_goals -> navController.navigate(R.id.goalsFragment)
                R.id.nav_weight -> {
                    // Handle navigation to WeightFragment
                }
                R.id.nav_medication -> navController.navigate(R.id.medicationFragment)
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
