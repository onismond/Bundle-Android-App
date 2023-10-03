package com.bundlegh.bundle.ui.home.delivery_location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentCheckoutBinding
import com.bundlegh.bundle.databinding.FragmentDeliveryLocationBinding
import com.bundlegh.bundle.ui.home.HomeViewModel
import com.bundlegh.bundle.ui.home.HomeViewModelFactory
import com.bundlegh.bundle.util.BaseFragment
import com.bundlegh.bundle.util.BitmapHelper
import com.bundlegh.bundle.util.snackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.ktx.addMarker
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DeliveryLocationFragment : BaseFragment(), KodeinAware, OnMapReadyCallback {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    override var bottomNavigationViewVisibility = View.INVISIBLE
    private lateinit var binding: FragmentDeliveryLocationBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0
    private var locationPermissionGranted = true
    private var mapInitialized = false
    private var lastKnownLocation: Location? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null
    private val zoom = 18F
    private val locationIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(activity?.applicationContext!!, R.color.primary)
        BitmapHelper.vectorToBitmap(activity?.applicationContext!!, R.drawable.add_icon, color)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryLocationBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        Places.initialize(activity?.applicationContext!!, getString(R.string.maps_api_key))
        placesClient = Places.createClient(activity?.applicationContext!!)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
        val mapFragment = binding.mapView
        mapFragment.onCreate(null)
        mapFragment.onResume()
        mapFragment.getMapAsync(this)
        binding.buttonConfirmLocation.setOnClickListener{
            confirmLocation()
        }
        return binding.root
    }

    private fun confirmLocation() {
        val location = "${marker?.position?.latitude},${marker?.position?.longitude}"
        if (location.equals("null,null")) {
            binding.root.snackbar("Please wait to select your location on the map")
            return
        }
        if (location.equals("5.115526355346435,-1.2915171682834625")) {
            binding.root.snackbar("Please select your location on the map")
            return
        }
        val bundle = Bundle()
        bundle.putString("delivery_location", location)
        this.findNavController().navigate(
            R.id.action_deliveryLocationFragment_to_checkoutFragment,
            bundle
        )
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = googleMap.addMarker {
                position(latLng)
                title("Delivery Location")
                icon(locationIcon)
            }
            updateLocationUI()
        }
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    updateLocationUI()
                    getDeviceLocation()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(activity?.applicationContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            Log.e("E", "Permissions granted")
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            Log.e("E", "Permissions denied")
            setDefaultLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (googleMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                googleMap?.isMyLocationEnabled = false
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            val latLng = LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude)
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
                            marker = googleMap.addMarker {
                                position(latLng)
                                title("Delivery Location")
                                icon(locationIcon)
                            }
                        } else {
                            setDefaultLocation()
                        }
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                    } else {
                        setDefaultLocation()
                    }
                }
            } else {
                setDefaultLocation()
            }
        } catch (e: SecurityException) {
            setDefaultLocation()
        }
    }

    private fun setDefaultLocation() {
        val ucc = LatLng(5.115526355346435, -1.2915171682834625)
        if (marker == null) {
            marker = googleMap.addMarker {
                position(ucc)
                title("Delivery Location")
                icon(locationIcon)
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ucc, zoom))
        }
    }




}