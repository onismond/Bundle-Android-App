package com.pietech.bundle.ui.home.category.sub_category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.SubCategory
import com.pietech.bundle.databinding.FragmentCategoryBinding
import com.pietech.bundle.databinding.FragmentSubCategoryBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.home.category.CategoryFragmentDirections
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SubCategoryFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentSubCategoryBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: ProductAdapter
    private var subCategoryId = ""
    private var categoryName = ""
    private lateinit var subCategory: SubCategory
    private lateinit var product: Product
    private var savedSubCategory: SubCategory? = null
    private var savedProduct: Product? = null
    private var itemPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]
        subCategoryId = arguments?.getString("sub_category_id") ?: ""
        categoryName = arguments?.getString("category_name") ?: ""
        setup()
        return binding.root
    }

    private fun setup() {
        Coroutines.io {
            if (savedSubCategory == null) {
                subCategory = viewModel.getSubCategory(subCategoryId)
                savedSubCategory = subCategory
            } else {
                subCategory = savedSubCategory as SubCategory
            }
            activity?.runOnUiThread {
                binding.recyclerViewProducts.apply {
                    adapter = productAdapter.apply {
                        onProductClicked = { product, pos ->
                            itemPosition = pos
                            productClicked(product)
                        }
                    }
                    layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                }
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        Coroutines.io {
            try {
                if (savedProduct == null) {
                    product = viewModel.getCurrentProduct() as Product
                    savedProduct = product
                } else {
                    product = savedProduct as Product
                }
                val response = viewModel.getSubCategoryProducts(subCategory.id)
                if (response.isSuccessful!!){
                    productAdapter = ProductAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {

                    }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
    }

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = SubCategoryFragmentDirections.actionSubCategoryFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = SubCategoryFragmentDirections.actionSubCategoryFragmentToProductFragment()
            this.findNavController().navigate(action)
        }

    }
}