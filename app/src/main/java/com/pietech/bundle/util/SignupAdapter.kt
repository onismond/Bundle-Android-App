package com.pietech.bundle.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pietech.bundle.ui.auth.signup.OneFragment
import com.pietech.bundle.ui.auth.signup.ThreeFragment
import com.pietech.bundle.ui.auth.signup.TwoFragment
import com.pietech.bundle.ui.splash_screen.FirstFragment
import com.pietech.bundle.ui.splash_screen.SecondFragment
import com.pietech.bundle.ui.splash_screen.ThirdFragment

class SignupAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    val fragments = listOf<Fragment>(OneFragment(), TwoFragment(), ThreeFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}