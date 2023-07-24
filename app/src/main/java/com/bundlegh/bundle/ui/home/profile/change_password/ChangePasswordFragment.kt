package com.bundlegh.bundle.ui.home.profile.change_password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentChangePasswordBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ChangePasswordFragment : BaseFragment(), KodeinAware {

    override var bottomNavigationViewVisibility = View.INVISIBLE
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        binding.buttonChangePassword.setOnClickListener {
            changePassword()
        }
        return binding.root
    }

    private fun changePassword() {
        Coroutines.io {
            activity?.runOnUiThread {
                val oldPassword = binding.editTextOldPassword.text.toString()
                val newPassword = binding.editTextNewPassword.text.toString()
                val newPasswordConfirm = binding.editTextNewPasswordConfirm.text.toString()
                if (oldPassword.isNullOrEmpty()) {
                    binding.root.snackbar("Your old password is required")
                } else if (newPassword.isNullOrEmpty()) {
                    binding.root.snackbar("Please Enter a unique password")
                } else if (newPasswordConfirm.isNullOrEmpty()) {
                    binding.root.snackbar("Please confirm your new password")
                } else if (newPassword != newPasswordConfirm) {
                    binding.root.snackbar("New Password does not match")
                } else {
                    Coroutines.io {
                        try {
                            activity?.runOnUiThread { (activity as HomeActivity).showProgress() }
                            val response = viewModel.changePassword(oldPassword, newPassword)
                            activity?.runOnUiThread {
                                (activity as HomeActivity).hideProgress()
                                binding.root.snackbar(response.detail)
                            }
                        } catch (e: ApiException) {
                            activity?.runOnUiThread {
                                (activity as HomeActivity).hideProgress()
                                binding.root.snackbar(e.message!!)
                            }
                        } catch (e: NoInternetException) {
                            activity?.runOnUiThread {
                                (activity as HomeActivity).hideProgress()
                                binding.root.snackbar(e.message!!)
                            }
                        } catch (e: Exception) {
                            activity?.runOnUiThread {
                                (activity as HomeActivity).hideProgress()
                                binding.root.snackbar("Sorry, an error occurred")
                            }
                        }

                    }
                }
            }
        }
    }

}









