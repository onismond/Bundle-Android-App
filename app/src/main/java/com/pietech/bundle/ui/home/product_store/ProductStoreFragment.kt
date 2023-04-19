package com.pietech.bundle.ui.home.product_store

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentProductBinding
import com.pietech.bundle.databinding.FragmentProductStoreBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.home.product.ProductFragment
import com.pietech.bundle.ui.home.product.ProductFragmentDirections
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProductStoreFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentProductStoreBinding
    private lateinit var product: Product
    private lateinit var buttonBuyNow: Button
    private lateinit var buttonAddToCart: Button
    var savedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductStoreBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]
        setup()
        return binding.root
    }

    private fun setup() {
        val circularProgressDrawable = CircularProgressDrawable(requireActivity().applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Coroutines.io {
            if (savedProduct == null) {
                product = viewModel.getCurrentProduct() as Product
                savedProduct = product
            } else {
                product = savedProduct as Product
            }
            activity?.runOnUiThread {
                binding.textViewProductName.text = product.name
                binding.textViewAmount.text = product.amount
                binding.textViewLocation.text = product.location
                binding.textViewDescription.text = product.description
                Glide.with(requireActivity().applicationContext)
                    .load(product.image)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.error_placeholder)
                    .skipMemoryCache(true) //for caching the image url in case phone is offline
                    .into(binding.imageViewMain)
                setupRecyclerAdapter()

                buttonBuyNow = binding.buttonBuyNow
                buttonAddToCart = binding.buttonAddToCart
                buttonBuyNow.setOnClickListener {
                    buyNow(it)
                }
                buttonAddToCart.setOnClickListener {
                    addToCart(it)
                }
            }
        }
    }

    private fun setupRecyclerAdapter(){
        Coroutines.io {
            try {
                val response = viewModel.getSimilarProducts(product.id!!)
                if (response.isSuccessful!!) {
                    val similarProductsAdapter = ProductSmallAdapter(response.products!!, activity?.applicationContext!!)
                    activity?.runOnUiThread {
                        binding.recyclerViewSimilar.apply {
                            adapter = similarProductsAdapter.apply {
                                onProductClicked = { product ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = false }
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

    private fun buyNow(view: View){
        Coroutines.io {
            viewModel.addToCart(product.toCart())
        }
        val homeActivity = activity as HomeActivity
        homeActivity.navigateToCart()
    }

    private fun addToCart(view: View){
        Coroutines.io {
            viewModel.addToCart(product.toCart())
        }
        view.snackbar("Item added to Bundle successfully")
    }

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = ProductStoreFragmentDirections.actionProductStoreFragmentSelf()
            this.findNavController().navigate(action)
        } else {
            val action = ProductStoreFragmentDirections.actionProductStoreFragmentToProductFragment()
            this.findNavController().navigate(action)
        }

    }

    companion object {
        var listState: Parcelable? = null
        val LIST_STATE_KEY = "list_state"
//        var savedProduct: Product? = null
        var savedProducts: MutableList<Product>? = null
        var itemPosition: Int = 0
    }

}