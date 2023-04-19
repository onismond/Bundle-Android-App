package com.pietech.bundle.ui.home.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentHomeBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.image_slider.BottomAdsAdapter
import com.pietech.bundle.ui.image_slider.ImageSliderAdapter
import com.pietech.bundle.ui.image_slider.ImageSliderFragment
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
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var bottomAdsAdapter: BottomAdsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var handler : Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]

        recyclerViewFeatured = binding.recyclerViewFeatured
        recyclerViewNewlyAdded = binding.recyclerViewNewlyAdded
        recyclerViewPopular = binding.recyclerViewPopular

        setupImageSlider()
        setupRecyclerAdapter()
        setImageSliderHandler()
        setUpCategories()
        setupBottomAds()
        loadCategories()
        loadSchools()
        return binding.root
    }


    override fun onPause() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        super.onPause()
    }

    override fun onResume() {
        if (handler == null) {
            setImageSliderHandler()
        }
        super.onResume()
    }

    private fun setImageSliderHandler() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                binding.viewPagerImageSlider.scroll()
                Log.e("Error", "scroll")
                handler?.postDelayed(this, 4000)
            }
        })
    }

    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(childFragmentManager, lifecycle)
        viewModel.getSliderImages().observe(viewLifecycleOwner) { imageSliders ->
            for (imageSlider in imageSliders) {
                if (!imageSliderAdapter.idExists(imageSlider.id)) {
                    imageSliderAdapter.addFragment(ImageSliderFragment.newInstance(
                        imageSlider.id,
                        imageSlider.image,
                        imageSlider.product_id,
                        imageSlider.store_id
                    ), imageSlider.id)
                }
            }
        }
        binding.viewPagerImageSlider.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = imageSliderAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImageSlider) { tab, position ->
        }.attach()
    }

    private fun setupBottomAds() {
        bottomAdsAdapter = BottomAdsAdapter(mutableListOf(), mutableListOf(), requireActivity().applicationContext)
        viewModel.getBottomAds().observe(viewLifecycleOwner) { bottomAds ->
            for (bottomAd in bottomAds) {
                if (!bottomAdsAdapter.idExists(bottomAd.id)) {
                    bottomAdsAdapter.addBottomAd(bottomAd, bottomAd.id)
                }
            }
        }
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewAdsBottom.apply {
            layoutManager = linearLayoutManager
            adapter = bottomAdsAdapter
        }
        bottomAdsAdapter.onBottomAdClicked = { bottomAd ->
            binding.root.snackbar(bottomAd.id)
        }
    }

    private fun productClicked(product: Product){
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = HomeFragmentDirections.actionHomeFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = HomeFragmentDirections.actionHomeFragmentToProductFragment()
            this.findNavController().navigate(action)
        }

    }

    private fun setUpCategories() {
        categoryAdapter = CategoryAdapter(mutableListOf(), mutableListOf(), requireActivity().applicationContext)
        categoryAdapter.onCategoryClicked = { category ->
            val bundle = Bundle()
            bundle.putString("category_id", category.id)
            this.findNavController().navigate(
                R.id.action_homeFragment_to_categoryFragment,
                bundle,
            )
        }
        viewModel.getCategories().observe(viewLifecycleOwner) { categories ->
            for (category in categories) {
                if (!categoryAdapter.idExists(category.id)) {
                    categoryAdapter.addCategory(category, category.id)
                }
            }
        }
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewCategories.apply {
            layoutManager = linearLayoutManager
            adapter = categoryAdapter
        }
    }

    private fun setupRecyclerAdapter() {
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
                                onProductClicked = { product, pos ->
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
                                onProductClicked = { product, pos ->
                                    productClicked(product)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
                        }
                    }
                    for (sliderImage in indexResponse.slider_images)
                        viewModel.saveSliderImage(sliderImage)
                    for (bottomAd in indexResponse.bottom_ads)
                        viewModel.saveBottomAd(bottomAd)

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

    private fun loadCategories() {
        Coroutines.io {
            try {
                val categoryResponse = viewModel.loadCategories()
                for (category in categoryResponse.categories)
                    viewModel.saveCategory(category)
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
        Coroutines.io {
            try {
                val subCategoryResponse = viewModel.loadSubCategories()
                for (subCategory in subCategoryResponse.sub_categories)
                    viewModel.saveSubCategory(subCategory)
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
    }

    private fun loadSchools() {
        Coroutines.io {
            try {
                val schoolResponse = viewModel.loadSchools()
                for (school in schoolResponse.schools)
                    viewModel.saveSchool(school)
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
    }

}












