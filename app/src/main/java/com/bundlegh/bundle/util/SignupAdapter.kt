package com.bundlegh.bundle.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bundlegh.bundle.ui.auth.signup.OneFragment
import com.bundlegh.bundle.ui.auth.signup.ThreeFragment
import com.bundlegh.bundle.ui.auth.signup.TwoFragment
import com.bundlegh.bundle.ui.splash_screen.FirstFragment
import com.bundlegh.bundle.ui.splash_screen.SecondFragment
import com.bundlegh.bundle.ui.splash_screen.ThirdFragment

class SignupAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    val fragments = listOf<Fragment>(OneFragment(), TwoFragment(), ThreeFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}