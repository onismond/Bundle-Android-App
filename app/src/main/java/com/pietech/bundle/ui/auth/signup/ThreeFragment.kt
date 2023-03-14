package com.pietech.bundle.ui.auth.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pietech.bundle.databinding.FragmentThreeBinding
import com.pietech.bundle.ui.auth.AuthViewModel
import com.pietech.bundle.ui.auth.AuthViewModelFactory
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import org.kodein.di.KodeinAware

class ThreeFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding = FragmentThreeBinding.inflate(inflater)
        return binding.root
    }

    fun getPassword() = binding.editTextPassword.text.toString()

    fun getPasswordConfirm() = binding.editTextPasswordConfirm.text.toString()

}