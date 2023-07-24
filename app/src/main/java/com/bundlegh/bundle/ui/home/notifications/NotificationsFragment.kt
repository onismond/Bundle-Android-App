package com.bundlegh.bundle.ui.home.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentNotificationsBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.BaseFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NotificationsFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentNotificationsBinding
    override var bottomNavigationViewVisibility = View.INVISIBLE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        binding = FragmentNotificationsBinding.inflate(layoutInflater)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {

    }
}