package com.bundlegh.bundle.ui.home.update_product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.UserProduct
import com.bundlegh.bundle.data.preferences.PreferenceProvider
import com.bundlegh.bundle.databinding.FragmentUpdateProductBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import com.google.android.material.tabs.TabLayoutMediator
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class UpdateProductFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentUpdateProductBinding
    private lateinit var mainImageAdapter: MainImageAdapter
    private var product = UserProduct()
    private var productId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProductBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        productId = arguments?.getString("product_id") ?: ""
        setup()
        binding.buttonCancel.setOnClickListener {
            goBack()
        }
        binding.buttonUpdate.setOnClickListener {
            upload()
        }
        return binding.root
    }

    private fun setup() {
        mainImageAdapter = MainImageAdapter(childFragmentManager, lifecycle)
        val linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        Coroutines.io {
            try {
                product = viewModel.getUserProduct(productId)
                activity?.runOnUiThread{
                    binding.editTextName.setText(product.name ?: "")
                    binding.editTextAmount.setText(product.amount ?: "")
                    binding.editTextDescription.setText(product.description ?: "")
                    mainImageAdapter.addFragment(
                        MainImageFragment.newInstance(
                            "0",
                            product.image.toString()
                        ), "0"
                    )
                    setupImages()
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
                activity?.runOnUiThread { binding.root.snackbar(e.message!!) }
                Log.e("E", e.message.toString())
            }
        }
    }

    private fun setupImages() {
        Coroutines.io {
            try {
                val response = viewModel.getProductImages(product.id)
                if (response.isSuccessful) {
                    response.images?.let { images ->
                        activity?.runOnUiThread {
                            for (image in images) {
                                mainImageAdapter.addFragment(
                                    MainImageFragment.newInstance(
                                        image.id,
                                        image.image,
                                    ), image.id
                                )
                            }
                        }
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

        binding.viewPagerImageSlider.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = mainImageAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImageSlider) { tab, position ->
        }.attach()

    }

    private fun upload() {
        val name = binding.editTextName.text.toString()
        val amount = binding.editTextAmount.text.toString()
        val description = binding.editTextDescription.text.toString()
        showProgress()
        Coroutines.io {
            try {
                val response = viewModel.updateUserProduct(productId, name, amount, description)
                if (response.isSuccessful!!) {
                    activity?.runOnUiThread {
                        binding.root.snackbar(response.detail)
                    }
                } else {
                    binding.root.snackbar(response.detail)
                }
                activity?.runOnUiThread{hideProgress()}
            } catch (e: ApiException) {
                activity?.runOnUiThread{hideProgress()}
                binding.root.snackbar(e.message!!)
            } catch (e: NoInternetException) {
                activity?.runOnUiThread{hideProgress()}
                binding.root.snackbar(e.message!!)
            } catch (e: Exception){
                activity?.runOnUiThread{hideProgress()}
                binding.root.snackbar("Sorry, an error occurred. Please try again")
            }
        }
    }

    private fun goBack() {
        activity?.onBackPressed()
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }

}