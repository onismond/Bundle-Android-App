package com.pietech.bundle.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pietech.bundle.databinding.ActivityHomeBinding
import com.pietech.bundle.databinding.ActivityLoginBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}