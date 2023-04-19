package com.pietech.bundle.ui.home.search

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.databinding.FragmentSearchBinding
import com.pietech.bundle.ui.home.HomeViewModel
import com.pietech.bundle.ui.home.HomeViewModelFactory
import com.pietech.bundle.ui.home.home.HomeFragmentDirections
import com.pietech.bundle.ui.home.product.ProductFragmentDirections
import com.pietech.bundle.util.*
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var editTextSearch: SearchView
    private lateinit var textViewKeyword: TextView
    private lateinit var recyclerViewSearch: RecyclerView
    private lateinit var searchAdapter: ProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("Error", "On create view")
        println(listState)
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        editTextSearch = binding.editTextSearch
        textViewKeyword = binding.textViewKeyword
        recyclerViewSearch = binding.recyclerViewSearch
        editTextSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0.toString())
                Log.e("Error", "Search clicked")
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
//        binding.root.setOnFocusChangeListener { view, b ->
//
//        }
//        if (itemPosition != 0) {
//            return null
//        }
        setup()
        setupRecyclerView()
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e("Error", "On save instance state")
        listState = binding.recyclerViewSearch.layoutManager?.onSaveInstanceState()
        outState.putParcelable(LIST_STATE_KEY, listState)
        println(listState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("Error", "On view state restored")
        println(listState)
//        if(listState != null)
//            listState = savedInstanceState?.getParcelable(LIST_STATE_KEY)
    }

    override fun onResume() {
        super.onResume()
        Log.e("Error", "On resume")
        println(listState)
//        binding.recyclerViewSearch.
        if (listState != null) {
            Log.e("Error", binding.recyclerViewSearch.layoutManager.toString())
            binding.recyclerViewSearch.layoutManager?.onRestoreInstanceState(listState)
        } else {
            search("")
        }
    }

    override fun onDestroy() {
        Log.e("Error", "On destroy")
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("Error", "On view created")
        println(listState)
//        search(binding.editTextSearch.query.toString())
    }

    private fun setupRecyclerView() {
        if (savedProducts != null) {
            searchAdapter.setProducts(savedProducts!!)
            Coroutines.main {
                repeat(2) {
                    delay(2000)
                    activity?.runOnUiThread{
                        (binding.recyclerViewSearch.layoutManager as GridLayoutManager).scrollToPosition(itemPosition)
                        Log.e("Error", "Scrolled $it times $itemPosition")
                    }
                }
            }
//            activity?.runOnUiThread {
//                recyclerViewSearch.adapter = searchAdapter.apply {
//                    onProductClicked = { product, pos ->
//                        productClicked(product, pos)
//                    }
//                }
//                val gridLayoutManager = GridLayoutManager(activity?.applicationContext!!, 2)
//                recyclerViewSearch.layoutManager = gridLayoutManager
//                recyclerViewSearch.layoutManager?.scrollToPosition(itemPosition)
//                Log.e("Error", itemPosition.toString())
////                gridLayoutManager.setTargetStartPos(itemPosition, 1)
//            }
        }
    }private fun setup() {
        searchAdapter = ProductAdapter(mutableListOf(), requireActivity().applicationContext)
        recyclerViewSearch.apply {
            adapter = searchAdapter.apply {
                onProductClicked = { product, pos ->
                    productClicked(product, pos)
                }
            }
            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
        }
//        binding.textViewKeyword.text = keyword

    }



    private fun search(keyword: String){
        Coroutines.io {
            try {
                val searchResponse = viewModel.getSearchItems(keyword)
                if (!searchResponse.empty) {
                    savedProducts = searchResponse.products!!
//                    searchAdapter = ProductAdapter(searchResponse.products!!, requireActivity().applicationContext)
                    activity?.runOnUiThread {
//                        recyclerViewSearch.apply {
//                            adapter = searchAdapter.apply {
//                                onProductClicked = { product, pos ->
//                                    productClicked(product, pos)
//                                }
//                            }
//                            layoutManager = object: GridLayoutManager(activity?.applicationContext, 2) { override fun canScrollVertically() = false }
//                        }
                        searchAdapter.setProducts(searchResponse.products)
                        binding.textViewKeyword.text = keyword
                    }
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            }
        }
    }

    private fun productClicked(product: Product, pos: Int){
        itemPosition = pos
        Coroutines.io {
            viewModel.saveProduct(product)
        }
        if (product.is_store!!) {
            val action = SearchFragmentDirections.actionSearchFragmentToProductStoreFragment()
            this.findNavController().navigate(action)
        } else {
            val action = SearchFragmentDirections.actionSearchFragmentToProductFragment()
            this.findNavController().navigate(action)
        }

    }

    companion object {
        var listState: Parcelable? = null
        val LIST_STATE_KEY = "list_state"
        var savedProducts: MutableList<Product>? = null
        var itemPosition: Int = 0
    }

}