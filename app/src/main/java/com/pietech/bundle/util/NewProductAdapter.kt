package com.pietech.bundle.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pietech.bundle.ui.new_product.ProductFourFragment
import com.pietech.bundle.ui.new_product.ProductOneFragment
import com.pietech.bundle.ui.new_product.ProductThreeFragment
import com.pietech.bundle.ui.new_product.ProductTwoFragment

class NewProductAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    val fragments = listOf<Fragment>(ProductOneFragment(), ProductTwoFragment(), ProductThreeFragment(), ProductFourFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}