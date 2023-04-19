package com.pietech.bundle.ui.home.category

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.SubCategory
import com.pietech.bundle.databinding.ActivityHomeBinding
import com.pietech.bundle.databinding.FragmentCategoryBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.home.home.HomeFragmentDirections
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CategoryFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: ProductAdapter
    private var categoryId = ""
    private lateinit var category: Category
    private lateinit var subCategory: SubCategory
    private lateinit var subCategoryAdapter: SubCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]
        categoryId = arguments?.getString("category_id") ?: ""
        setup()
        Log.e("E", "category id $categoryId")
        return binding.root
    }

    private fun setup() {
        Coroutines.io {
            category = viewModel.getCategory(categoryId)
            activity?.runOnUiThread {
                Log.e("E", "category name is ${category.name}")
                setupSubCategory()
                setupRecyclerView()
            }
        }
    }

    private fun setupSubCategory() {
        subCategoryAdapter = SubCategoryAdapter(mutableListOf(), mutableListOf(), requireActivity().applicationContext)
        subCategoryAdapter.onSubCategoryClicked = { subCategory ->
                        val bundle = Bundle()
                        bundle.putString("sub_category_id", subCategory.id)
                        bundle.putString("category_name", category.name)
                        this.findNavController().navigate(
                            R.id.action_categoryFragment_to_subCategoryFragment,
                            bundle,
                        )
        }
        viewModel.getSubCategories(category.name!!).observe(viewLifecycleOwner) { subCategories ->
            activity?.runOnUiThread {
                for (subCategory in subCategories) {
                    if (!subCategoryAdapter.idExists(subCategory.id)) {
                        subCategoryAdapter.addSubCategory(subCategory, subCategory.id)
                    }
                }
            }
        }
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewSubCategories.apply {
            layoutManager = linearLayoutManager
            adapter = subCategoryAdapter
        }
    }

    private fun setupRecyclerView() {
        Coroutines.io {
            try {
                val response = viewModel.getCategoryProduct(category.id)
                if (response.isSuccessful!!){
                    productAdapter = ProductAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {
                        binding.recyclerViewProducts.apply {
                            adapter = productAdapter.apply {
                                onProductClicked = { product, pos ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                        }
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
            val action = CategoryFragmentDirections.actionCategoryFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = CategoryFragmentDirections.actionCategoryFragmentToProductFragment()
            this.findNavController().navigate(action)
        }

    }

}