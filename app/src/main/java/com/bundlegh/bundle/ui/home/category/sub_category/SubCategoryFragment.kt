package com.bundlegh.bundle.ui.home.category.sub_category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.databinding.FragmentCategoryBinding
import com.bundlegh.bundle.databinding.FragmentSubCategoryBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.category.CategoryFragmentDirections
import com.bundlegh.bundle.util.*
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
        setupRecyclerView()
        binding.buttonReload.setOnClickListener {
            setupRecyclerView()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        Coroutines.io {
            try {
                subCategory = viewModel.getSubCategory(subCategoryId)
                activity?.runOnUiThread {
                    binding.textViewName.text = subCategory.name
                    Glide.with(requireActivity().applicationContext)
                        .load(subCategory.image)
                        .placeholder(R.drawable.loading_placeholder)
                        .error(R.drawable.error_placeholder)
                        .skipMemoryCache(true) //for caching the image url in case phone is offline
                        .into(binding.imageView)
                }
                activity?.runOnUiThread { showProgress() }
                val response = viewModel.getSubCategoryProducts(subCategoryId)
                if (response.isSuccessful!!){
                    productAdapter = ProductAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {
                        hideProgress()
                        binding.recyclerViewProducts.apply {
                            adapter = productAdapter.apply {
                                onProductClicked = { product, pos ->
                                    itemPosition = pos
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
                }
//                Log.e("E", e.message.toString())
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