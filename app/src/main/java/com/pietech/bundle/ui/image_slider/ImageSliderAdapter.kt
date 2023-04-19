package com.pietech.bundle.ui.image_slider

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageSliderAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(
    fragmentManager, lifecycle
) {
    private val imageFragments = mutableListOf<ImageSliderFragment>()
    private val ids = mutableListOf<String>()

    override fun createFragment(position: Int): Fragment {
        return imageFragments[position]
    }

    fun addFragment(fragment: ImageSliderFragment, id: String) {
        imageFragments.add(fragment)
        ids.add(id)
        notifyItemInserted(imageFragments.indexOf(fragment))
    }

    fun idExists(id: String): Boolean{
        if (id in ids)
            return true
        return false
    }

    override fun getItemCount(): Int {
        return imageFragments.size
    }
}