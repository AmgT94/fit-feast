package com.example.fitfeast

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitfeast.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    interface DrawerController {
        fun setDrawerLocked(locked: Boolean)
    }

    private var drawerController: DrawerController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DrawerController) {
            drawerController = context
            drawerController?.setDrawerLocked(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.dashboard)
        fetchNews()
    }

    private fun fetchNews() {
        lifecycleScope.launch {
            try {
                val newsResponse = RetrofitInstance.api.getEverything(apiKey = "a275ac9dc51b4c989ab5a7e77ee714ea", query = "health, fitness, exercise")
                if (newsResponse.status == "ok" && newsResponse.articles.isNotEmpty()) {
                    Log.d("DashboardFragment", "Articles fetched: ${newsResponse.articles.size}")
                    activity?.runOnUiThread {
                        setupNewsViewPager(newsResponse.articles)
                    }
                } else {
                    Log.e("DashboardFragment", "No articles received or status not OK.")
                }
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Error fetching news", e)
            }
        }
    }

    private fun setupNewsViewPager(articles: List<Article>) {
        val adapter = NewsArticleAdapter(articles)
        binding.newsViewPager.adapter = adapter
        // Link ViewPager2 with DotsIndicator
        binding.dotsIndicator.setViewPager2(binding.newsViewPager)
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.dashboard)
    }

    override fun onDetach() {
        super.onDetach()
        drawerController?.setDrawerLocked(true)
        drawerController = null
    }
}
