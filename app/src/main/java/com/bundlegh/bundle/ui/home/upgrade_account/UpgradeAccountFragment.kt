package com.bundlegh.bundle.ui.home.upgrade_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.AccountType
import com.bundlegh.bundle.databinding.FragmentUpgradeAccountBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class UpgradeAccountFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentUpgradeAccountBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpgradeAccountBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setup()
        binding.buttonReload.setOnClickListener {
            setup()
        }
        return binding.root
    }

    private fun setup() {
        Coroutines.io {
            try {
                activity?.runOnUiThread { showProgress() }
                val response = viewModel.getAccountTypes()
                if (response.isSuccessful!!) {
                    activity?.runOnUiThread {
                        val adapter = AccountTypeAdapter(response.account_types)
                        adapter.onAccountTypeClicked = { accountType ->
                            accountTypeClicked(accountType)
                        }
                        binding.recyclerViewAccount.adapter = adapter
                        binding.recyclerViewAccount.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                    }
                }
                activity?.runOnUiThread { hideProgress() }
            } catch (e: ApiException) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar("Sorry, an error occurred")
                }
                e.printStackTrace()
            }
        }
    }

    private fun accountTypeClicked(accountType: AccountType) {

    }

    private fun showProgress() {
        binding.recyclerViewAccount.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBarText.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBarText.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.recyclerViewAccount.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progressBarText.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewAccount.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }

}