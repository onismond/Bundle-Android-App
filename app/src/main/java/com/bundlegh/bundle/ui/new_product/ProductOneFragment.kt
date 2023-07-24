package com.bundlegh.bundle.ui.new_product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentProductOneBinding

class ProductOneFragment : Fragment() {

    private lateinit var binding: FragmentProductOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductOneBinding.inflate(inflater)
        return binding.root
    }

    fun getName() = binding.editTextName.text.toString()

    fun getAmount() = binding.editTextAmount.text.toString()

}