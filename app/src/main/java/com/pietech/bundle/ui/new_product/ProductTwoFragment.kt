package com.pietech.bundle.ui.new_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pietech.bundle.databinding.FragmentProductTwoBinding
import com.pietech.bundle.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProductTwoFragment : Fragment(), KodeinAware, AdapterView.OnItemSelectedListener {

    override val kodein by kodein()
    private val factory: NewProductViewModelFactory by instance()
    private lateinit var viewModel: NewProductViewModel
    private lateinit var binding: FragmentProductTwoBinding

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductTwoBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[NewProductViewModel::class.java]
        subCategoryAdapter = SubCategoryAdapter()
        binding.spinnerSubCategories.onItemSelectedListener = subCategoryAdapter
        setupCategories()
        return binding.root
    }
    
    private fun setupCategories() {
        categoryAdapter = CategoryAdapter()
        categoryAdapter.onCategoryClicked = {categoryName ->
            setupSubCategories(categoryName)
        }
        binding.spinnerCategories.onItemSelectedListener = categoryAdapter
        viewModel.getCategories().observe(viewLifecycleOwner) {categories ->
            val categoryNames = mutableListOf<String>()
            for (category in categories)
                categoryNames.add(category.name!!)
            val categoryAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activity?.runOnUiThread {
                binding.spinnerCategories.adapter = categoryAdapter
            }
        }
    }
    
    private fun setupSubCategories(categoryName: String) {
        viewModel.getSubCategories(categoryName).observe(viewLifecycleOwner) {subCategories ->
            val subCategoryNames = mutableListOf<String>()
            for (subCategory in subCategories)
                subCategoryNames.add(subCategory.name!!)
            val subCategoryAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, subCategoryNames)
            subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activity?.runOnUiThread {
                binding.spinnerSubCategories.adapter = subCategoryAdapter
            }
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}


    fun getCategory() = categoryAdapter.category

    fun getSubCategory() = subCategoryAdapter.subCategory

}

class CategoryAdapter: AdapterView.OnItemSelectedListener {

    var category = ""
    lateinit var onCategoryClicked: ((String) -> Unit)

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        val categoryName: String = parent?.getItemAtPosition(pos).toString()
        category = categoryName
        onCategoryClicked(categoryName)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}

class SubCategoryAdapter: AdapterView.OnItemSelectedListener {
    var subCategory = ""
    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        subCategory = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}

