package com.bundlegh.bundle.ui.home.category

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
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.databinding.ActivityHomeBinding
import com.bundlegh.bundle.databinding.FragmentCategoryBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.home.HomeFragmentDirections
import com.bundlegh.bundle.util.*
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
    private lateinit var subCategoryAdapter: SubCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]
        categoryId = arguments?.getString("category_id") ?: ""
        setup()
        binding.buttonReload.setOnClickListener {
            setupRecyclerView()
        }
        return binding.root
    }

    private fun setup() {
        Coroutines.io {
            category = viewModel.getCategory(categoryId)
            activity?.runOnUiThread {
                binding.textViewName.text = category.name
                Glide.with(requireActivity().applicationContext)
                    .load(category.image)
                    .placeholder(R.drawable.loading_placeholder)
                    .error(R.drawable.error_placeholder)
                    .skipMemoryCache(true) //for caching the image url in case phone is offline
                    .into(binding.imageView)
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
                activity?.runOnUiThread { showProgress() }
                val response = viewModel.getCategoryProduct(category.id)
                if (response.isSuccessful!!){
                    productAdapter = ProductAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {
                        hideProgress()
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
                activity?.runOnUiThread {
                    showError()
//                    binding.root.snackbar(e.message!!)
                }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread {
                    showError()
//                    binding.root.snackbar(e.message!!)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    showError()
//                    binding.root.snackbar("Please check your internet connection")
                }
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

    private fun showProgress() {
        binding.recyclerViewProducts.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
        binding.recyclerViewProducts.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progress.visibility = View.GONE
        binding.recyclerViewProducts.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }
}