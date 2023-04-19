package com.pietech.bundle.ui.image_slider

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentImageSliderBinding
import kotlinx.android.synthetic.main.item_product.view.*

private const val ID = ""
private const val IMAGE = ""
private const val PRODUCT_ID = "0"
private const val STORE_ID = "0"

class ImageSliderFragment : Fragment() {

    private var id: String? = null
    private var image: String? = null
    private var productId: String? = null
    private var storeId: String? = null
    private lateinit var binding: FragmentImageSliderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageSliderBinding.inflate(inflater)
        arguments?.let {
            id = it.getString(ID)
            image = it.getString(IMAGE)
            productId = it.getString(PRODUCT_ID)
            storeId = it.getString(STORE_ID)
            Glide.with(activity?.applicationContext!!)
                .load(image)
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_placeholder)
                .skipMemoryCache(true) //for caching the image url in case phone is offline
                .into(binding.imageView)
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(id: String, image: String, productId: String, storeId: String) =
            ImageSliderFragment().apply {
                arguments = Bundle().apply {
                    putString(ID, id)
                    putString(IMAGE, image)
                    putString(PRODUCT_ID, productId)
                    putString(STORE_ID, storeId)
                }
            }
    }
}