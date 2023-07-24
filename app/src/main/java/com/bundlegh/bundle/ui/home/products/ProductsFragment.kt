package com.bundlegh.bundle.ui.home.products

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.UserProduct
import com.bundlegh.bundle.databinding.FragmentNewProductBinding
import com.bundlegh.bundle.databinding.FragmentProductsBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.new_product.NewProductActivity
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProductsFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: FragmentProductsBinding
    private var products: MutableList<UserProduct>? = null

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
        viewModel.getUserProducts().observe(viewLifecycleOwner) { _products ->
            products = _products
            if (products?.size == 0) {
                productsEmpty()
            } else {
                productsNotEmpty()
                val productAdapter = UserProductAdapter(products!!, activity?.applicationContext!!)
                productAdapter.onProductClicked = { userProduct ->
                    update(userProduct)
                }
                productAdapter.onProductLongClicked = { product ->
                    val homeActivity = activity as HomeActivity
                    homeActivity.alertAction(product)
                }
                activity?.runOnUiThread {
                    binding.recyclerViewProducts.adapter = productAdapter
                    binding.recyclerViewProducts.layoutManager = GridLayoutManager(requireActivity().applicationContext, 2)
                }
            }
        }
    }

    private fun getProducts() {
        Coroutines.io {
            try {
                val userProductResponse = viewModel.geUserProducts()
                if (userProductResponse.isSuccessful!!) {
                    for (product in userProductResponse.products!!) {
                        if (products?.contains(product) != true)
                            viewModel.saveUserProduct(product)
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

    private fun onNewProductClicked() {
        val action = ProductsFragmentDirections.actionProductsFragmentToNewProductActivity()
        this.findNavController().navigate(action)
//        Intent(requireActivity().applicationContext, NewProductActivity::class.java).also {
//            startActivity(it)
//        }
    }

    private fun update(userProduct: UserProduct) {
        val bundle = Bundle()
        bundle.putString("product_id", userProduct.id)
        this.findNavController().navigate(
            R.id.action_productsFragment_to_updateProductFragment,
            bundle,
        )
    }

    private fun productsEmpty() {
        binding.productsEmpty.visibility = View.VISIBLE
    }

    private fun productsNotEmpty() {
        binding.productsEmpty.visibility = View.INVISIBLE
    }

}