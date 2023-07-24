package com.bundlegh.bundle.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bundlegh.bundle.ui.new_product.ProductFourFragment
import com.bundlegh.bundle.ui.new_product.ProductOneFragment
import com.bundlegh.bundle.ui.new_product.ProductThreeFragment
import com.bundlegh.bundle.ui.new_product.ProductTwoFragment

class NewProductAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    val fragments = listOf<Fragment>(ProductOneFragment(), ProductTwoFragment(), ProductThreeFragment(), ProductFourFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}