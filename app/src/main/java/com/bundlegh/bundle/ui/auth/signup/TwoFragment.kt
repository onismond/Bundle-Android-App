package com.bundlegh.bundle.ui.auth.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentTwoBinding
import com.bundlegh.bundle.ui.auth.AuthViewModel
import com.bundlegh.bundle.ui.auth.AuthViewModelFactory
import com.bundlegh.bundle.util.SchoolAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TwoFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentTwoBinding
    private lateinit var schoolAdapter: SchoolAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding = FragmentTwoBinding.inflate(inflater)
        setupSchools()
        return binding.root
    }

    fun getEmail() = binding.editTextEmail.text.toString()

    fun getSchool() = schoolAdapter.school

    private fun setupSchools() {
        schoolAdapter = SchoolAdapter()
        binding.spinnerSchool.onItemSelectedListener = schoolAdapter
        viewModel.getSchools().observe(viewLifecycleOwner) { schools ->
            val schoolNames = mutableListOf<String>()
            for (school in schools)
                schoolNames.add(school.name!!)
            val schoolAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, schoolNames)
            schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activity?.runOnUiThread {
                binding.spinnerSchool.adapter = schoolAdapter
            }
        }

    }

}