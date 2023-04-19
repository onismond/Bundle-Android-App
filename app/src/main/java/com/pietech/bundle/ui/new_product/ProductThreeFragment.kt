package com.pietech.bundle.ui.new_product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentProductThreeBinding

class ProductThreeFragment : Fragment() {

    private lateinit var binding: FragmentProductThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductThreeBinding.inflate(inflater)
        return binding.root
    }

    fun getDescription() = binding.editTextDescription.text.toString()
}