package com.bundlegh.bundle.ui.splash_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.R
import com.bundlegh.bundle.databinding.ActivitySplashScreenBinding
import com.bundlegh.bundle.ui.auth.AuthViewModel
import com.bundlegh.bundle.ui.auth.AuthViewModelFactory
import com.bundlegh.bundle.ui.auth.LoginActivity
import com.bundlegh.bundle.ui.splash_screen.WelcomeActivity
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.ui.maps.MapsActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashScreenActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getLoggedInUser().observe(this) { user ->
                if (user != null) {
                    intent = Intent(this, HomeActivity::class.java)
//                    intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    intent = Intent(this, WelcomeActivity::class.java)
//                    intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, 1000)
    }
}