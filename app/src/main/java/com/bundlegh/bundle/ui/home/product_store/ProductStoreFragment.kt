package com.bundlegh.bundle.ui.home.product_store

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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.databinding.FragmentProductBinding
import com.bundlegh.bundle.databinding.FragmentProductStoreBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.product.ProductFragment
import com.bundlegh.bundle.ui.home.product.ProductFragmentDirections
import com.bundlegh.bundle.util.*
import com.google.android.material.tabs.TabLayoutMediator
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
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var mainImageAdapter: MainImageAdapter
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

        mainImageAdapter = MainImageAdapter(childFragmentManager, lifecycle)
        productImageAdapter = ProductImageAdapter(mutableListOf())
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewImages.apply {
            layoutManager = linearLayoutManager
            adapter = productImageAdapter.apply {
                onImageClicked = { position ->
                    imageClicked(position)
                }
            }
        }
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
                binding.editTextSellerName.text = product.owner_name
                binding.textViewOpenTime.text = viewModel.formatTime(product.open_time ?:"")
                binding.textViewCloseTime.text = viewModel.formatTime(product.close_time ?:"")

                Glide.with(requireActivity().applicationContext)
                    .load(product.owner_image)
                    .placeholder(R.drawable.loading_placeholder)
                    .centerCrop()
                    .error(R.drawable.error_placeholder)
                    .skipMemoryCache(true) //for caching the image url in case phone is offline
                    .into(binding.imageViewSeller)
                productImageAdapter.addMainImage(product.image!!)
                mainImageAdapter.addFragment(
                    MainImageFragment.newInstance(
                        "0",
                        product.image.toString()
                    ), "0"
                )
                setupImages()
                setupRecyclerAdapter()
                buttonBuyNow = binding.buttonBuyNow
                buttonAddToCart = binding.buttonAddToCart
                buttonBuyNow.setOnClickListener {
                    buyNow(it)
                }
                buttonAddToCart.setOnClickListener {
                    addToCart(it)
                }
                binding.constraintSeller.setOnClickListener {
                    sellerClicked()
                }
                binding.imageViewSeller.clipToOutline = true
            }
        }
    }

    private fun setupImages() {
        try {
            Coroutines.io {
                val response = viewModel.getProductImages(product.id!!)
                if (response.isSuccessful) {
                    response.images?.let { images ->
                        activity?.runOnUiThread {
                            for (image in images) {
                                productImageAdapter.addImage(image)
                                mainImageAdapter.addFragment(
                                    MainImageFragment.newInstance(
                                        image.id,
                                        image.image,
                                    ), image.id)
                            }
                        }
                    }
                }
            }
        } catch (e: ApiException) {
            activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
        } catch (e: NoInternetException) {
            activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
        }

        binding.viewPagerImageSlider.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = mainImageAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImageSlider) { tab, position ->
        }.attach()

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
            } catch (e: Exception) {
                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
    }

    private fun imageClicked(position: Int) {
        binding.viewPagerImageSlider.scrollTo(position)
    }

    private fun sellerClicked() {
        val bundle = Bundle()
        bundle.putString("store_id", product.owner_id)
        bundle.putString("name", product.owner_name)
        bundle.putString("image", product.owner_image)
        bundle.putString("open_time", product.open_time)
        bundle.putString("close_time", product.close_time)
        bundle.putString("location", product.location)
        this.findNavController().navigate(
            R.id.action_productStoreFragment_to_storeFragment,
            bundle
        )
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