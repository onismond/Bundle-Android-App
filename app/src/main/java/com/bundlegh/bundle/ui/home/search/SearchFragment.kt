package com.bundlegh.bundle.ui.home.search

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
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.preferences.PreferenceProvider
import com.bundlegh.bundle.databinding.FragmentSearchBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.ui.home.home.HomeFragmentDirections
import com.bundlegh.bundle.ui.home.product.ProductFragmentDirections
import com.bundlegh.bundle.util.*
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SearchFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        editTextSearch = binding.editTextSearch
        textViewKeyword = binding.textViewKeyword
        recyclerViewSearch = binding.recyclerViewSearch
        editTextSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search(p0.toString())
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        binding.buttonReload.setOnClickListener {
            search(editTextSearch.query.toString())
        }
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
        val keyword = preference.getSearchKeyword()
//        keyword?.also {
//
//        }
        if (keyword != null) {
            binding.editTextSearch.setQuery(keyword, true)
//            binding.textViewKeyword.text = it
//            search(it)
            preference.removeSearchKeyword()
        }
        else if (listState != null) {
            Log.e("Error", binding.recyclerViewSearch.layoutManager.toString())
            binding.recyclerViewSearch.layoutManager?.onRestoreInstanceState(listState)
            hideProgress()
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
                activity?.runOnUiThread { showProgress() }
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
                } else {
                    activity?.runOnUiThread {
                        binding.textViewKeyword.text = keyword
                        emptyResult()
                    }
                    return@io
                }
                activity?.runOnUiThread { hideProgress() }
            } catch (e: ApiException) {
                activity?.runOnUiThread { showError() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { showError() }
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
                activity?.runOnUiThread { showError() }
                activity?.runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
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

    private fun showProgress() {
        binding.recyclerViewSearch.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.empty.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
        binding.empty.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.recyclerViewSearch.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progress.visibility = View.GONE
        binding.recyclerViewSearch.visibility = View.GONE
        binding.empty.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }

    private fun emptyResult() {
        binding.progress.visibility = View.GONE
        binding.recyclerViewSearch.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.empty.visibility = View.VISIBLE
    }

    companion object {
        var listState: Parcelable? = null
        val LIST_STATE_KEY = "list_state"
        var savedProducts: MutableList<Product>? = null
        var itemPosition: Int = 0
    }

}