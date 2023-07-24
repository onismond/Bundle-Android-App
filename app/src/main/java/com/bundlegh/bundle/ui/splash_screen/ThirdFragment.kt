package com.bundlegh.bundle.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bundlegh.bundle.databinding.FragmentThirdBinding
import com.bundlegh.bundle.ui.auth.LoginActivity
import com.bundlegh.bundle.ui.auth.signup.SignupActivity

class ThirdFragment : Fragment() {

    private lateinit var binding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThirdBinding.inflate(layoutInflater)
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSignup.setOnClickListener {
            val intent = Intent(activity, SignupActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

}