package com.pietech.bundle.ui.home.checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.databinding.FragmentCheckoutBinding
import com.pietech.bundle.databinding.FragmentMyProductsBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CheckoutFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        setCartTotal()

        binding.buttonConfirnOrder.setOnClickListener {
            confirmOrder()
        }

        return binding.root
    }

    private fun setCartTotal() {
        var total = 0.0
        var currentAmount = ""
        viewModel.getCartItems().observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                total = 0.00
            } else {
                for (cart in cartItems) {
                    currentAmount = cart.amount ?: "0.0"
                    total += currentAmount.toDouble()
                }
            }
            activity?.runOnUiThread {
                binding.textViewTotal.text = total.toString()
            }
        }
    }

    private fun confirmOrder() {
        var cartList = ""
        Coroutines.io {
            try {
                val cartItems = viewModel.getCurrentCart()
                for (cart in cartItems){
                    cartList += cart.id + "," + cart.quantity + ",,"
                }
                val response = viewModel.submitOrder(cartList)
                if (response.isSuccessful!!){
                    viewModel.addToOrders(Order(response.order_id, response.amount, response.status))
                }

                activity?.runOnUiThread {
//                    binding.textViewMap.text = cartList
                    binding.root.snackbar(response.toString())
                    val homeActivity = activity as HomeActivity
                    homeActivity.navigateToProfile()
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
    }

}