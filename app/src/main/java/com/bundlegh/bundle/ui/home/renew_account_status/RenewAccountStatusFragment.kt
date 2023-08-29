package com.bundlegh.bundle.ui.home.renew_account_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.databinding.FragmentRenewAccountBinding
import com.bundlegh.bundle.databinding.FragmentRenewAccountStatusBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RenewAccountStatusFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentRenewAccountStatusBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var transactionId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        binding = FragmentRenewAccountStatusBinding.inflate(inflater)
        transactionId = arguments?.getString("transactionId") ?: ""
        loadStatus()
        binding.buttonGoBack.setOnClickListener {
            goBack()
        }
        binding.buttonReload.setOnClickListener {
            loadStatus()
        }
        //
        // Todo API endpoints: renewAccount(contact: str), transactionStatus() -> status: str, completed: bool
        //
        return binding.root
    }

    private fun goBack() {
        (activity as HomeActivity).go_back_twice()
    }

    private fun loadStatus() {
        showProgress()
        Coroutines.io {
            try {
                val response = viewModel.transactionStatus(transactionId)
                if (response.isSuccessful!!) {
                    if (response.completed){
                        activity?.runOnUiThread { showSuccess(response.status, response.detail) }
                    } else {
                        activity?.runOnUiThread { showFailed() }
                    }
                } else {
                    activity?.runOnUiThread { showError() }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { showError() }
            } catch (e: Exception) {
                activity?.runOnUiThread { showError() }
            }

        }
    }

    private fun showProgress() {
        binding.success.visibility = View.GONE
        binding.failed.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
    }

    private fun showSuccess(status: String, detail: String) {
        binding.progress.visibility = View.GONE
        binding.textViewStatus.text = status
        binding.textViewDetail.text = detail
        binding.success.visibility = View.VISIBLE

    }

    private fun showFailed() {
        binding.progress.visibility = View.GONE
        binding.failed.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progress.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }

}