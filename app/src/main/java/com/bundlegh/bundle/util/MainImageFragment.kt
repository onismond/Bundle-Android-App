package com.bundlegh.bundle.util

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.FragmentImageSliderBinding
import com.bundlegh.bundle.databinding.FragmentMainImageBinding
import kotlinx.android.synthetic.main.item_product.view.*

private const val ID = ""
private const val IMAGE = ""

class MainImageFragment : Fragment() {

    private var id: String? = null
    private var image: String? = null
    private var productId: String? = null
    private var storeId: String? = null
    private lateinit var binding: FragmentMainImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainImageBinding.inflate(inflater)
        arguments?.let {
            id = it.getString(ID)
            image = it.getString(IMAGE)
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
        fun newInstance(id: String, image: String) =
            MainImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ID, id)
                    putString(IMAGE, image)
                }
            }
    }
}