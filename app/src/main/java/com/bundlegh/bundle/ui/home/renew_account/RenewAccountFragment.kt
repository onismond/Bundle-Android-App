package com.bundlegh.bundle.ui.home.renew_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentRenewAccountBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RenewAccountFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentRenewAccountBinding
    private lateinit var viewModel: HomeViewModel
    private var transactionId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRenewAccountBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        val amount = arguments?.getString("amount")
        binding.textViewAmount.text = amount
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
        binding.buttonContinue.setOnClickListener {
            confirm()
        }
        return binding.root
    }

    private fun submit() {
        val contact = binding.editTextContact.text.toString()
        if (contact.isEmpty()) {
            binding.root.snackbar("Please enter your contact")
        } else {
            Coroutines.io {
                try {
                    val response = viewModel.renewAccount(contact)
                    if (response.isSuccessful!!) {
                        transactionId = response.transaction_id
                        activity?.runOnUiThread { showConfirmVerify() }
                    }
                } catch (e: ApiException) {
                    activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
                } catch (e: NoInternetException) {
                    activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity?.runOnUiThread { binding.root.snackbar("Please check your internet connection") }
                }

            }
        }
    }

    private fun confirm() {
        val bundle = Bundle()
        bundle.putString("transactionId", transactionId)
        this.findNavController().navigate(
            R.id.action_renewAccountFragment_to_renewAccountStatusFragment,
            bundle
        )
    }

    private fun showConfirmVerify() {
        binding.progress.visibility = View.GONE
        binding.confirmVerify.visibility = View.VISIBLE
    }
}