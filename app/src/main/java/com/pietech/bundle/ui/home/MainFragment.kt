package com.pietech.bundle.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.setupWithNavController
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentMainBinding
import com.pietech.bundle.util.snackbar


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

//        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
//        val navController = activity?.findNavController()
        val navController = findNavController(this)
        binding.bottomNavigationView.setupWithNavController(navController!!)
//        setHasOptionsMenu(true)

        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.app_nav, menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_notifications -> {
//            true
//        }
//
//        R.id.action_refresh -> {
//            binding.root.snackbar("Refresh")
//            println("Refresh")
//            true
//        }
//
//        R.id.action_logout -> {
//
//            true
//        }
//
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onSupportNavigateUp() = findNavController(R.id.nav_fragment).navigateUp()


}