package com.pietech.bundle.ui.home.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.databinding.FragmentOrdersBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class OrdersFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        recyclerViewOrders = binding.recyclerViewOrders

        setupRecyclerAdapter()
        return binding.root
    }

    private fun setupRecyclerAdapter(){
        viewModel.getOrders().observe(viewLifecycleOwner) { orders ->
            orders ?: binding.root.snackbar("No orders yet")
            orderAdapter = OrderAdapter(orders)
            activity?.runOnUiThread {
                recyclerViewOrders.apply {
                    adapter = orderAdapter
                    layoutManager = object: LinearLayoutManager(activity?.applicationContext) { override fun canScrollVertically() = true}
                }

            }
        }
        Coroutines.io {
            try {
                val ordersResponse = viewModel.loadOrders()
                if (ordersResponse.isSuccessful!!) {
                    for (order in ordersResponse.orders) {
                        viewModel.addToOrders(order)
                    }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }

    }
}