package com.pietech.bundle.ui.home.product_store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentProductBinding
import com.pietech.bundle.databinding.FragmentProductStoreBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.BaseFragment
import com.pietech.bundle.util.Coroutines
import com.pietech.bundle.util.snackbar
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductStoreBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[(HomeViewModel::class.java)]

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
        }
        buttonBuyNow = binding.buttonBuyNow
        buttonAddToCart = binding.buttonAddToCart
        buttonBuyNow.setOnClickListener {
            buyNow(it)
        }
        buttonAddToCart.setOnClickListener {
            addToCart(it)
        }
        return binding.root
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
}