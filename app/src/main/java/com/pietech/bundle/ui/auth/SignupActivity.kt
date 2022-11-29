package com.pietech.bundle.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pietech.bundle.databinding.ActivitySignupBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.util.ApiException
import com.pietech.bundle.util.Coroutines
import com.pietech.bundle.util.NoInternetException
import com.pietech.bundle.util.snackbar
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SignupActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.getLoggedInUser().observe(this) { user ->
            if (user != null) {
                Intent(this, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        }

        binding.buttonSignUp.setOnClickListener {
            userSignup()
        }

        binding.textViewLogin.setOnClickListener {
            startLoginActivity()
        }

    }

    private fun userSignup() {
        val name = binding.editTextName.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()
        val passwordConfirm = binding.editTextPasswordConfirm.text.toString()

        // Input validation
        if(name.isNullOrEmpty()){
            binding.rootLayout.snackbar("Name is required")
            return
        }
        if(username.isNullOrEmpty()){
            binding.rootLayout.snackbar("Name is required")
            return
        }
        if(email.isNullOrEmpty()){
            binding.rootLayout.snackbar("Email is required")
            return
        }
        if(password.isNullOrEmpty()){
            binding.rootLayout.snackbar("Please enter a password")
            return
        }
        if (password != passwordConfirm){
            binding.rootLayout.snackbar("Password does not match,, $password , $passwordConfirm")
            return
        }

        Coroutines.io {
            try {
                val authResponse = viewModel.userSignup(name, username, email, password)
                if (authResponse.isSuccessful == true) {
                    binding.rootLayout.snackbar("Registration Successful\nPlease login to continue")
                    startLoginActivity()
                } else {
                    binding.rootLayout.snackbar(authResponse.message!!)
                }
            } catch (e: ApiException) {
                binding.rootLayout.snackbar(e.message!!)
            } catch (e: NoInternetException) {
                binding.rootLayout.snackbar(e?.message!!)
            }
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}