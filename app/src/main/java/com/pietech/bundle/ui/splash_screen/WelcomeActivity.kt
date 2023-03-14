package com.pietech.bundle.ui.splash_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pietech.bundle.databinding.ActivityWelcomeBinding
import com.pietech.bundle.util.WelcomeAdapter

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var welcomeAdapter: WelcomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        viewPager2 = binding.viewPager2
        welcomeAdapter = WelcomeAdapter(supportFragmentManager, lifecycle)
        viewPager2.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = welcomeAdapter
        }
        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, position ->
        }.attach()
        setContentView(binding.root)
    }
}