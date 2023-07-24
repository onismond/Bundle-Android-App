package com.bundlegh.bundle.ui.home.my_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bundlegh.bundle.data.db.entities.Profile
import com.bundlegh.bundle.databinding.FragmentMyAccountBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.BaseFragment
import com.bundlegh.bundle.util.Coroutines
import com.bundlegh.bundle.util.ProductViewsAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MyAccountFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentMyAccountBinding
    private lateinit var viewModel: HomeViewModel
    private var profile: Profile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyAccountBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setup()
        return binding.root
    }

    private fun setup(){
        viewModel.getProfile().observe(viewLifecycleOwner) { _profile ->
            profile = _profile
            profile?.apply {
                binding.textViewName.text = account_name
                binding.textViewAmount.text = amount
                binding.textViewProductCount.text = product_count
                binding.textViewProductsCount.text = products_count
                binding.textViewLastRenewal.text = last_payment
                binding.textViewNextRenewal.text = next_payment
            }
        }
        loadProfile()
        loadProducts()
        binding.constraintMain.setOnClickListener {
            upgradeAccount()
        }
        binding.buttonRenewAccount.setOnClickListener {
            renewAccount()
        }
    }

    private fun loadProfile() {
        Coroutines.io {
            try {
                val response = viewModel.loadProfile()
                if (profile !=response.profile)
                    viewModel.saveProfile(response.profile)
            } catch (e: Exception) {
//
            }

        }
    }

    private fun loadProducts() {
        viewModel.getUserProducts().observe(viewLifecycleOwner) { products ->
            val productAdapter = ProductViewsAdapter(products!!, activity?.applicationContext!!)
            activity?.runOnUiThread {
                binding.recyclerViewProducts.adapter = productAdapter
                binding.recyclerViewProducts.layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = false }
            }
        }
    }

    private fun upgradeAccount() {
        val action = MyAccountFragmentDirections.actionMyAccountFragmentToUpgradeAccountFragment()
        this.findNavController().navigate(action)
    }

    private fun renewAccount() {
        val action = MyAccountFragmentDirections.actionMyAccountFragmentToRenewAccountFragment()
        this.findNavController().navigate(action)
    }
}