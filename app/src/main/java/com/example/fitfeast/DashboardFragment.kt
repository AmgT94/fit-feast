package com.example.fitfeast

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.fitfeast.databinding.FragmentDashboardBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using binding
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.dashboard)
    }


    override fun onResume() {
        super.onResume()
        // Set the title in the ActionBar
        //(activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.dashboard)
    }

    override fun onDetach() {
        super.onDetach()
        // Lock the drawer when fragment detaches if necessary
        drawerController?.setDrawerLocked(true)
        drawerController = null
    }
}
