package com.bundlegh.bundle.ui.home.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Order
import com.bundlegh.bundle.databinding.FragmentCheckoutBinding
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CheckoutFragment : BaseFragment(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: HomeViewModel
    private var deliveryLocation = ""
    private lateinit var timeStartAdapter: TimeAdapter
    private lateinit var timeEndAdapter: TimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        deliveryLocation = arguments?.getString("delivery_location") ?: ""
        setCartTotal()
        setup()
        binding.buttonConfirnOrder.setOnClickListener {
            confirmOrder()
        }
        return binding.root
    }

    private fun setCartTotal() {
        Coroutines.io {
            var total = 0.0
            var currentAmount = ""
            val cartItems = viewModel.getCartItems()
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
                val startTime = timeStartAdapter.time.toString()
                val endTime = timeEndAdapter.time.toString()
                val contact = binding.editTextContact.text.toString()
                if (contact.isEmpty()) {
                    binding.root.snackbar("Please provide your mobile number")
                    return@io
                }
                if (endTime == "7") {
                    binding.root.snackbar("Please select the duration in which you will be available")
                    return@io
                }
                activity?.runOnUiThread { (activity as HomeActivity).showProgress() }
                val cartItems = viewModel.getCurrentCart()
                for (cart in cartItems){
                    cartList += cart.id + "," + cart.quantity + ",,"
                }
                val response = viewModel.submitOrder(cartList, deliveryLocation, contact, startTime, endTime)
                if (response.isSuccessful!!){
                    viewModel.addToOrders(Order(response.order_id, response.amount, response.status))
                }
                activity?.runOnUiThread {
//                    binding.textViewMap.text = cartList
                    binding.root.snackbar(response.detail)
                    val homeActivity = activity as HomeActivity
                    homeActivity.hideProgress()
                    homeActivity.navigateToProfile()
                }
            } catch (e: ApiException) {
                activity?.runOnUiThread {
                    (activity as HomeActivity).hideProgress()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: NoInternetException) {
                activity?.runOnUiThread {
                    (activity as HomeActivity).hideProgress()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    (activity as HomeActivity).hideProgress()
                    binding.root.snackbar("Sorry an error occurred. Please try again")
                }
            }
        }
    }

    private fun setup() {
        timeStartAdapter = TimeAdapter()
        timeEndAdapter = TimeAdapter()
        binding.spinnerStartTime.onItemSelectedListener = timeStartAdapter
        binding.spinnerEndTime.onItemSelectedListener = timeEndAdapter
        val timeArray = listOf(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        val timeAdapter = ArrayAdapter(activity?.applicationContext!!, R.layout.item_spinner, timeArray)
        binding.spinnerStartTime.adapter = timeAdapter
        binding.spinnerEndTime.adapter = timeAdapter
    }

//    private fun setupMap() {
//        lifecycleScope.launchWhenCreated {
//            val mapFragment: SupportMapFragment =
//                childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
//            val googleMap: GoogleMap = mapFragment.awaitMap()
//            val ucc = LatLng(5.108262993, -1.300581696)
//            googleMap.addMarker {
//                position(ucc)
//                title("Marker in UCC")
//            }
//        }
//    }
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                // When clicked on map
//                // Initialize marker options
//                MarkerOptions markerOptions=new MarkerOptions();
//                // Set position of marker
//                markerOptions.position(latLng);
//                // Set title of marker
//                markerOptions.title(latLng.latitude+" : "+latLng.longitude);
//                // Remove all marker
//                googleMap.clear();
//                // Animating to zoom the marker
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                // Add marker on map
//                googleMap.addMarker(markerOptions);
//            }
//    }

}