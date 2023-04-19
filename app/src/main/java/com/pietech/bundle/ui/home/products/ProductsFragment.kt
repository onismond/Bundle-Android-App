package com.pietech.bundle.ui.home.products

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.UserProduct
import com.pietech.bundle.databinding.FragmentNewProductBinding
import com.pietech.bundle.databinding.FragmentProductsBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.new_product.NewProductActivity
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProductsFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setupRecyclerView()
        getProducts()
        binding.buttonAdd.setOnClickListener {
            onNewProductClicked()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        viewModel.getUserProducts().observe(viewLifecycleOwner) { products ->
            val productAdapter = UserProductAdapter(products!!, activity?.applicationContext!!)
            productAdapter.onUserProductClicked = { userProduct ->
                onProductClicked(userProduct)
            }
            activity?.runOnUiThread {
                binding.recyclerViewProducts.adapter = productAdapter
                binding.recyclerViewProducts.layoutManager = GridLayoutManager(requireActivity().applicationContext, 2)
            }
        }
    }

    private fun getProducts() {
        Coroutines.io {
            try {
                val userProductResponse = viewModel.geUserProducts()
                if (userProductResponse.isSuccessful!!) {
                    for (product in userProductResponse.products!!) {
                        viewModel.saveUserProduct(product)
                    }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }

        }
    }

    private fun onNewProductClicked() {
//        val action = ProductsFragmentDirections.actionProductsFragmentToNewProductFragment()
//        this.findNavController().navigate(action)
        Intent(requireActivity().applicationContext, NewProductActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun onProductClicked(userProduct: UserProduct) {
        binding.root.snackbar("user product ${userProduct.id} clicked")
    }

}