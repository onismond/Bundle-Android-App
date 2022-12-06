package com.pietech.bundle.ui.home.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentHomeBinding
import com.pietech.bundle.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HomeFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val factory: HomeViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]

        binding.button.setOnClickListener {
            it.snackbar("Button Clicked")
            val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
            it.findNavController().navigate(action)
        }

        return binding.root
    }


}