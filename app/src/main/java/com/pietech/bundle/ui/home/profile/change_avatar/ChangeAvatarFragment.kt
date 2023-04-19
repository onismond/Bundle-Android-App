package com.pietech.bundle.ui.home.profile.change_avatar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentChangeAvatarBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.BaseFragment
import com.pietech.bundle.util.Coroutines
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ChangeAvatarFragment : BaseFragment(), KodeinAware {

    override var bottomNavigationViewVisibility = View.INVISIBLE
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentChangeAvatarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeAvatarBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        binding.imageViewAvatar.clipToOutline = true
        setup()
        binding.buttonChange.setOnClickListener {
            val homeActivity = activity as HomeActivity
            homeActivity.startCamera()
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
}