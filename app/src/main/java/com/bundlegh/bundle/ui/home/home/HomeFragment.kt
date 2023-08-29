package com.bundlegh.bundle.ui.home.home

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
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.Store
import com.bundlegh.bundle.data.preferences.PreferenceProvider
import com.bundlegh.bundle.databinding.FragmentHomeBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.image_slider.BottomAdsAdapter
import com.bundlegh.bundle.ui.image_slider.ImageSliderAdapter
import com.bundlegh.bundle.ui.image_slider.ImageSliderFragment
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.net.SocketTimeoutException

class HomeFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val factory: HomeViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private lateinit var recyclerViewFeatured: RecyclerView
    private lateinit var recyclerViewStore: RecyclerView
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
        recyclerViewStore = binding.recyclerViewStore
        recyclerViewNewlyAdded = binding.recyclerViewNewlyAdded
        recyclerViewPopular = binding.recyclerViewPopular
        binding.viewPagerImageSlider.clipToOutline = true

        binding.buttonReload.setOnClickListener {
            setupRecyclerAdapter()
        }
        binding.buttonSearch.setOnClickListener {
            search()
        }

        setupImageSlider()
        setupRecyclerAdapter()
        setImageSliderHandler()
        setUpCategories()
        setupBottomAds()
        loadCategories()
        loadSchools()
        return binding.root
    }

    private fun search() {
        val keyword = binding.editTextSearchText.text.toString()
        preference.saveSearchKeyword(keyword)
        (activity as HomeActivity).navigateToSearch()
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
                        id = imageSlider.id,
                        image = imageSlider.image,
                        productId = imageSlider.product_id,
                        storeId = imageSlider.store_id
                    ), imageSlider.id)
//                    binding.root.snackbar(imageSlider.product_id+imageSlider.store_id)
                }
            }
        }
        imageSliderAdapter.onImageSliderClicked = {productId, storeId ->
            imageAdClicked(productId, storeId)
        }
        binding.viewPagerImageSlider.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = imageSliderAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImageSlider) { tab, position ->
        }.attach()
    }

    private fun imageAdClicked(productId: String?, storeId: String?) {
        (activity as? HomeActivity)?.showProgress()
        Coroutines.io {
            try {
                activity?.runOnUiThread { (activity as? HomeActivity)?.showProgress() }
                if (productId?.toInt() != 0){
                    val productResponse = viewModel.getProduct(productId ?: "")
                    if (productResponse.isSuccessful!!) {
                        activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                        productClicked(productResponse.product)
                    }
                } else if (storeId?.toInt() != 0){
                    val storeResponse = viewModel.getStore(storeId ?: "")
                    if (storeResponse.isSuccessful!!) {
                        activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                        storeClicked(storeResponse.store)
                    }
                }
                activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
            } catch (e: ApiException) {
                activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: SocketTimeoutException) {
                activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                activity?.runOnUiThread { binding.root.snackbar("Please check your internet connection") }
            } catch (e: Exception) {
                activity?.runOnUiThread { (activity as? HomeActivity)?.hideProgress() }
                e.printStackTrace()
            }
        }
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
            imageAdClicked(bottomAd.product_id, bottomAd.store_id)
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

    private fun storeClicked(store: Store) {
        val bundle = Bundle()
        bundle.putString("store_id", store.id)
        bundle.putString("name", store.name)
        bundle.putString("image", store.image)
        bundle.putString("open_time", store.open_time)
        bundle.putString("close_time", store.close_time)
        bundle.putString("location", store.location)
        this.findNavController().navigate(
            R.id.action_homeFragment_to_storeFragment,
            bundle
        )
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
                activity?.runOnUiThread { showProgress() }
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

                    val storeAdapter = StoreAdapter(indexResponse.stores!!, requireActivity().applicationContext)
                    activity?.runOnUiThread {
                        recyclerViewStore.apply {
                            adapter = storeAdapter.apply {
                                onStoreClicked = { store, _ ->
                                    storeClicked(store)
                                }
                            }
                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
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
                    activity?.runOnUiThread { hideProgress() }
                    for (sliderImage in indexResponse.slider_images)
                        viewModel.saveSliderImage(sliderImage)
                    for (bottomAd in indexResponse.bottom_ads)
                        viewModel.saveBottomAd(bottomAd)
                    activity?.runOnUiThread { (activity as HomeActivity).checkAppUpdates(indexResponse.latest_version) }

                } else {
                    activity?.runOnUiThread { showError() }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { showError() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { showError() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
                activity?.runOnUiThread { showError() }
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
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
//                activity?.runOnUiThread { showError() }
//                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
        Coroutines.io {
            try {
                val subCategoryResponse = viewModel.loadSubCategories()
                for (subCategory in subCategoryResponse.sub_categories)
                    viewModel.saveSubCategory(subCategory)
            } catch (e: ApiException) {
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
//                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
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
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
//                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
//                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
    }

    private fun showProgress() {
        binding.constraintProducts.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
        binding.constraintProducts.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progress.visibility = View.GONE
        binding.constraintProducts.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }

}












