package com.pietech.bundle.ui.home.cart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentCartBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CartFragment : BaseFragment(), KodeinAware {

    private lateinit var binding: FragmentCartBinding
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var cartAdapter: CartAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        recyclerViewCart = binding.recyclerViewCart

        binding.buttonConfirnOrder.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToCheckoutFragment()
            this.findNavController().navigate(action)
        }
        setup()
        setCartTotal()
        return binding.root
    }

    private fun setup(){
        viewModel.getCartItems().observe(viewLifecycleOwner){ cartItems ->
            cartItems ?: binding.root.snackbar("Cart Empty")

            cartAdapter = CartAdapter(cartItems, requireActivity().applicationContext)
            activity?.runOnUiThread {
                recyclerViewCart.apply {
                    adapter = cartAdapter.apply {
                        onIncreaseQuantityClicked = { cart ->
                            increaseQuantity(cart)
                        }
                        onDecreaseQuantityClicked = { cart ->
                            decreaseQuantity(cart)
                        }
                        onRemoveClicked = { cart ->
                            onRemove(cart)
                        }
                    }
                    layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = true }
                }
            }
        }

    }


    private fun increaseQuantity(cart: Cart) {
        binding.root.snackbar("Increase quantity")
    }

    private fun decreaseQuantity(cart: Cart) {
        binding.root.snackbar("Decrease quantity")
    }

    fun onRemove(cart: Cart) {
        Coroutines.io {
            viewModel.removeFromCart(cart)
        }
        cartAdapter.removeCartItem(cart)
        setCartTotal()
        binding.root.snackbar("Cart item removed")
    }

    fun setCartTotal() {
        var total = 0.0
        var currentAmount = ""
        viewModel.getCartItems().observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                total = 0.00
            } else {
                for (cart in cartItems) {
                    currentAmount =  cart.amount ?: "0.0"
                    total += currentAmount.toDouble()
                }
            }
            activity?.runOnUiThread {
                binding.textViewTotal.text = total.toString()
            }
        }

    }

}