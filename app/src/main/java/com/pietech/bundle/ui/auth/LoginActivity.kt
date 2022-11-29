package com.pietech.bundle.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pietech.bundle.databinding.ActivityLoginBinding
import com.pietech.bundle.ui.home.HomeActivity
import com.pietech.bundle.util.ApiException
import com.pietech.bundle.util.Coroutines
import com.pietech.bundle.util.NoInternetException
import com.pietech.bundle.util.snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }

        binding.textViewSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        // todo validate the inputs

        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            binding.rootLayout.snackbar("Invalid email or password")
            return
        }

        // for fragments use viewLifeCycle.lifeCycleScope

        Coroutines.io {
            try {
                val authResponse = viewModel.userLogin(email, password)
                if (authResponse.user != null) {
                    viewModel.saveLoggedInUser(authResponse.user)
                } else {
                    binding.rootLayout.snackbar(authResponse.message!!)
                }
            } catch (e: ApiException) {
                binding.rootLayout.snackbar(e.message!!)
            } catch (e: NoInternetException) {
                binding.rootLayout.snackbar(e.message!!)
            }
        }
    }
}