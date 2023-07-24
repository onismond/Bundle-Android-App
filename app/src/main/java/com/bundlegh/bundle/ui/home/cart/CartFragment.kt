package com.bundlegh.bundle.ui.home.cart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Cart
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.databinding.FragmentCartBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
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
            val action = CartFragmentDirections.actionCartFragmentToDeliveryLocationFragment()
            this.findNavController().navigate(action)
        }
        setup()
        setCartTotal()
        return binding.root
    }

    private fun setup(){
        Coroutines.io {
            val cartItems = viewModel.getCartItems()
            if (cartItems.size == 0) {
                activity?.runOnUiThread {
                    cartEmpty()
                }
            }
            cartAdapter = CartAdapter(cartItems, requireActivity().applicationContext)
            activity?.runOnUiThread {
                recyclerViewCart.also {
                    it.adapter = cartAdapter.also {
                        it.onIncreaseQuantityClicked = { cart ->
                            increaseQuantity(cart)
                            cartAdapter.increaseQuantity(cart)
                        }
                        it.onDecreaseQuantityClicked = { cart ->
                            decreaseQuantity(cart)
                            cartAdapter.decreaseQuantity(cart)
                        }
                        it.onRemoveClicked = { cart ->
                            onRemove(cart)
                        }
                    }
                    it.layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = true }
                }
            }
        }
    }


    private fun increaseQuantity(cart: Cart) {
        cart.quantity = cart.quantity + 1
        Coroutines.io {
            viewModel.updateCart(cart)
            activity?.runOnUiThread { setCartTotal() }
        }

    }

    private fun decreaseQuantity(cart: Cart) {
        if (cart.quantity > 1)
        cart.quantity = cart.quantity - 1
        Coroutines.io {
            viewModel.updateCart(cart)
            activity?.runOnUiThread { setCartTotal() }
        }
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
        Coroutines.io {
            var total = 0.0
            var currentAmount = ""
            val cartItems = viewModel.getCartItems()
            if (cartItems.isNullOrEmpty()) {
                total = 0.00
            } else {
                for (cart in cartItems) {
                    currentAmount =  cart.amount ?: "0.0"
                    total += currentAmount.toDouble() * cart.quantity
                }
            }
            activity?.runOnUiThread {
                binding.textViewTotal.text = total.toString()
            }
        }
    }

    private fun cartEmpty() {
        binding.cartEmpty.visibility = View.VISIBLE
    }

}