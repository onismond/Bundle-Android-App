package com.pietech.bundle.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentProfileBinding
import com.pietech.bundle.ui.auth.LoginActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.BaseFragment
import com.pietech.bundle.util.Coroutines
import kotlinx.android.synthetic.main.item_product.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        binding.imageViewAvatar.clipToOutline = true
        setup()
        binding.textViewMyAccount.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToMyProductsFragment()
            this.findNavController().navigate(action)
        }
        binding.textViewOrders.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToOrdersFragment()
            this.findNavController().navigate(action)
        }
        binding.textViewLogout.setOnClickListener {
            logout()
        }
        binding.textViewUpdateProfile.setOnClickListener {
            updateProfile()
        }
        binding.textViewChangeAvatar.setOnClickListener {
            changeAvatar()
        }
        binding.textViewChangePassword.setOnClickListener {
            changePassword()
        }
        return binding.root
    }

    private fun setup() {
        viewModel.getAvatar().observe(viewLifecycleOwner) { image ->
            activity?.runOnUiThread {
                Glide.with(requireActivity().applicationContext)
                    .load(image ?: R.drawable.profile_pic)
                    .placeholder(R.drawable.loading_placeholder)
                    .error(R.drawable.profile_pic)
                    .skipMemoryCache(true) //for caching the image url in case phone is offline
                    .into(binding.imageViewAvatar)
            }
        }
    }

    private fun logout() {
        Coroutines.io {
            viewModel.logout()
            activity?.runOnUiThread {
                Intent(requireActivity().applicationContext, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        }
    }

    private fun updateProfile() {
        val action = ProfileFragmentDirections.actionProfileFragmentToUpdateProfileFragment()
        this.findNavController().navigate(action)
    }

    private fun changeAvatar() {
        this.findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToChangeAvatarFragment()
        )
    }

    private fun changePassword() {
        this.findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment()
        )
    }

}