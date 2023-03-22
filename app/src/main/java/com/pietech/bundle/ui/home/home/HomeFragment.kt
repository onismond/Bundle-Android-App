package com.pietech.bundle.ui.home.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentHomeBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HomeFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val factory: HomeViewModelFactory by instance()
    private lateinit var recyclerViewFeatured: RecyclerView
    private lateinit var recyclerViewNewlyAdded: RecyclerView
    private lateinit var recyclerViewPopular: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]

        recyclerViewFeatured = binding.recyclerViewFeatured
        recyclerViewNewlyAdded = binding.recyclerViewNewlyAdded
        recyclerViewPopular = binding.recyclerViewPopular

//        binding.button.setOnClickListener {
//            it.snackbar("Button Clicked")
//            val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
//            it.findNavController().navigate(action)
//        }
//        activity.runOnUiThread {  }
        binding.cat1.setOnClickListener {
            it.snackbar("Category 1")
        }
        setupRecyclerAdapter()
        return binding.root
    }

//    override fun onResume() {
//        super.onResume()
//        setupRecyclerAdapter()
//
//    }

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = HomeFragmentDirections.actionHomeFragmentToProductFragment()
            this.findNavController().navigate(action)
        } else {
            val action = HomeFragmentDirections.actionHomeFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        }

    }

    private fun setupRecyclerAdapter(){
        Coroutines.io {
            try {
                val indexResponse = viewModel.getIndexProducts()
                if (indexResponse.isSuccessful!!) {
                    val featuredProductsAdapter = ProductFeaturedAdapter(indexResponse.featured!!, requireActivity().applicationContext)
                    activity?.runOnUiThread {
                        recyclerViewFeatured.apply {
                            adapter = featuredProductsAdapter.apply {
                                onProductClicked = { product ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = false }
                        }
                    }

                    val newlyAddedProductsAdapter = ProductAdapter(indexResponse.newly_added!!, requireActivity().applicationContext)
                    activity?.runOnUiThread {
                        recyclerViewNewlyAdded.apply {
                            adapter = newlyAddedProductsAdapter.apply {
                                onProductClicked = { product ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                        }
                    }

                    val popularProductsAdapter = ProductAdapter(indexResponse.popular!!, requireActivity().applicationContext)
                    activity?.runOnUiThread {
                        recyclerViewPopular.apply {
                            adapter = popularProductsAdapter.apply {
                                onProductClicked = { product ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                        }
                    }

                } else {
//                    Toast.makeText(activity, "Not successful", Toast.LENGTH_SHORT).show()
//                    binding.rootLayout.snackbar("Not successful")
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }


    }


}












