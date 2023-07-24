package com.bundlegh.bundle.ui.home.renew_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentRenewAccountBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.BaseFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RenewAccountFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentRenewAccountBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRenewAccountBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        return binding.root
    }
}