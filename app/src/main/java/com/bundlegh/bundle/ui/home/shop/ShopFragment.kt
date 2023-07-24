package com.bundlegh.bundle.ui.home.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.databinding.FragmentShopBinding
import com.bundlegh.bundle.databinding.FragmentStoreBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.store.StoreFragmentDirections
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ShopFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentShopBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var storeId:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        storeId = arguments?.getString("store_id") ?: ""
        val name = arguments?.getString("name") ?: ""
        val image = arguments?.getString("image") ?: ""
        val contact = arguments?.getString("contact") ?: ""
        val location = arguments?.getString("location") ?: ""
        binding.textViewName.text = name
        binding.textViewLocation.text = location
        binding.textViewContact.text = contact
        Glide.with(requireActivity().applicationContext)
            .load(image)
            .placeholder(R.drawable.loading_placeholder)
            .centerCrop()
            .error(R.drawable.error_placeholder)
            .skipMemoryCache(true) //for caching the image url in case phone is offline
            .into(binding.imageViewAvatar)
        binding.imageViewAvatar.clipToOutline = true
        binding.buttonReload.setOnClickListener {
            setup()
        }
        setup()
        return binding.root
    }

    private fun setup() {
        Coroutines.io {
            try {
                val response = viewModel.getStoreProducts(storeId)
                if (response.isSuccessful!!) {
                    activity?.runOnUiThread { showProgress() }
                    val productsAdapter = ProductAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {
                        binding.recyclerViewProducts.apply {
                            adapter = productsAdapter.apply {
                                onProductClicked = { product, _ ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                        }
                        hideProgress()
                    }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    showError()
                    binding.root.snackbar("Sorry, an error occurred")
                }
            }
        }

    }

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = ShopFragmentDirections.actionShopFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = ShopFragmentDirections.actionShopFragmentToProductFragment()
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
        binding.error.visibility = View.GONE
        binding.recyclerViewProducts.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progress.visibility = View.GONE
        binding.recyclerViewProducts.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }
}