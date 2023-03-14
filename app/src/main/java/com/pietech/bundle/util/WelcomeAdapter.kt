package com.pietech.bundle.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pietech.bundle.ui.splash_screen.FirstFragment
import com.pietech.bundle.ui.splash_screen.SecondFragment
import com.pietech.bundle.ui.splash_screen.ThirdFragment

class WelcomeAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(
    fragmentManager, lifecycle
) {
    val fragments = listOf<Fragment>(FirstFragment(), SecondFragment(), ThirdFragment())
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}