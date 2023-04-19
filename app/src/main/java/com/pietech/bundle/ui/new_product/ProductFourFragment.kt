package com.pietech.bundle.ui.new_product

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.pietech.bundle.R
import com.pietech.bundle.databinding.FragmentProductFourBinding
import com.pietech.bundle.util.ImageAdapter

class ProductFourFragment : Fragment() {

    private lateinit var binding: FragmentProductFourBinding
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductFourBinding.inflate(inflater)
        setupRecyclerView()
        binding.buttonAdd.setOnClickListener {
            startCamera()
        }
        return binding.root
    }


    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(mutableListOf())
        binding.recyclerViewImages.adapter = imageAdapter
        binding.recyclerViewImages.layoutManager = GridLayoutManager(requireActivity().applicationContext, 2)
    }

    private fun startCamera() {
        val newProductActivity = activity as NewProductActivity
        newProductActivity.startCamera()
    }

    fun addImage(image: Bitmap) = imageAdapter.addImage(image)

    fun getImages() = imageAdapter.getImages()

}