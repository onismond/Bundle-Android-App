package com.pietech.bundle.ui.auth.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentTwoBinding
import com.pietech.bundle.ui.auth.AuthViewModel
import com.pietech.bundle.ui.auth.AuthViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TwoFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding = FragmentTwoBinding.inflate(inflater)
        return binding.root
    }

    fun getEmail() = binding.editTextEmail.text.toString()

}