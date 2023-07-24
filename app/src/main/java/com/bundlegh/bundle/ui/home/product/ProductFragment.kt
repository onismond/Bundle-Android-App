package com.bundlegh.bundle.ui.home.product

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Image
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.databinding.FragmentProductBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.home.HomeFragmentDirections
import com.bundlegh.bundle.ui.image_slider.ImageSliderAdapter
import com.bundlegh.bundle.ui.image_slider.ImageSliderFragment
import com.bundlegh.bundle.util.*
import com.google.android.material.tabs.TabLayoutMediator
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class ProductFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentProductBinding
    private lateinit var product: Product
    private lateinit var productImageAdapter: ProductImageAdapter
    private lateinit var mainImageAdapter: MainImageAdapter
    var savedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]

        setup()
        return binding.root
    }

    private fun setup(){
//        val circularProgressDrawable = CircularProgressDrawable(requireActivity().applicationContext)
//        circularProgressDrawable.strokeWidth = 5f
//        circularProgressDrawable.centerRadius = 30f
//        circularProgressDrawable.start()

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
                Glide.with(requireActivity().applicationContext)
                    .load(product.owner_image)
                    .centerCrop()
                    .placeholder(R.drawable.loading_placeholder)
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
                binding.buttonViewContact.setOnClickListener {
                    viewContact()
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

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = ProductFragmentDirections.actionProductFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = ProductFragmentDirections.actionProductFragmentSelf()
            this.findNavController().navigate(action)
        }

    }

    private fun imageClicked(position: Int) {
        binding.viewPagerImageSlider.scrollTo(position)
    }

    private fun viewContact(){
        binding.buttonViewContact.text = product.owner_contact
    }
    private fun sellerClicked() {
        val bundle = Bundle()
        bundle.putString("store_id", product.owner_id)
        bundle.putString("name", product.owner_name)
        bundle.putString("image", product.owner_image)
        bundle.putString("contact", product.owner_contact)
        bundle.putString("location", product.location)
        this.findNavController().navigate(
            R.id.action_productFragment_to_shopFragment,
            bundle
        )
    }
//
//    companion object {
//        var listState: Parcelable? = null
//        val LIST_STATE_KEY = "list_state"
////        var savedProduct: Product? = null
//        var savedProducts: MutableList<Product>? = null
//        var itemPosition: Int = 0
//    }

}