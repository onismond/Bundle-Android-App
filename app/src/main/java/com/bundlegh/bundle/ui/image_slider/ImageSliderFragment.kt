package com.bundlegh.bundle.ui.image_slider

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentImageSliderBinding
import com.bundlegh.bundle.util.snackbar
import kotlinx.android.synthetic.main.item_product.view.*

private const val ID = "ID"
private const val IMAGE = "IMAGE"
private const val PRODUCT_ID = "PRODUCT_ID"
private const val STORE_ID = "STORE_ID"

class ImageSliderFragment : Fragment() {

    private var id: String? = null
    private var image: String? = null
    private var productId: String? = null
    private var storeId: String? = null
    private lateinit var binding: FragmentImageSliderBinding
    lateinit var onImageClicked: ((String?, String?) -> Unit)

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
                .placeholder(R.drawable.loading_placeholder2)
                .error(R.drawable.error_placeholder2)
                .skipMemoryCache(true) //for caching the image url in case phone is offline
                .into(binding.imageView)
        }
        binding.imageView.clipToOutline = true
        binding.imageView.setOnClickListener {
            arguments?.let {
//                binding.root.snackbar(it.getString(PRODUCT_ID).toString() + it.getString(STORE_ID).toString())
                onImageClicked.invoke(it.getString(PRODUCT_ID), it.getString(STORE_ID))
            }

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