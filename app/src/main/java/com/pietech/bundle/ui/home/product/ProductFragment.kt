package com.pietech.bundle.ui.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentProductBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.home.home.HomeFragmentDirections
import com.pietech.bundle.util.*
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
    private lateinit var buttonViewContact: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]
        setup()
        return binding.root
    }

    private fun setup(){
        val circularProgressDrawable = CircularProgressDrawable(requireActivity().applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        viewModel.getCurrentProduct().observe(viewLifecycleOwner) { item ->
            product = item
            binding.textViewProductName.text = item.name
            binding.textViewAmount.text = item.amount
            binding.textViewLocation.text = item.location
            binding.textViewDescription.text = item.description
            Glide.with(requireActivity().applicationContext)
                .load(item.image)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.error_placeholder)
                .skipMemoryCache(true) //for caching the image url in case phone is offline
                .into(binding.imageViewMain)
            setupRecyclerAdapter()
        }
        buttonViewContact = binding.buttonViewContact
        buttonViewContact.setOnClickListener {
            viewContact(it)
        }

    }

    private fun setupRecyclerAdapter(){
        Coroutines.io {
            try {
                val indexResponse = viewModel.getSimilarProducts(product.id!!)
                if (indexResponse.isSuccessful!!) {
                    val similarProductsAdapter = ProductSmallAdapter(indexResponse.products!!, requireActivity().applicationContext)
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

    private fun viewContact(view: View){
//        Coroutines.io {
//            viewModel.addToCart(product.toCart())
//        }
//        val homeActivity = activity as HomeActivity
//        homeActivity.navigateToCart()
    }

}