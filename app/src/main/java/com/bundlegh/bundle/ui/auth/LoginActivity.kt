package com.bundlegh.bundle.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bundlegh.bundle.databinding.ActivityLoginBinding
import com.bundlegh.bundle.ui.auth.signup.SignupActivity
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.util.ApiException
import com.bundlegh.bundle.util.Coroutines
import com.bundlegh.bundle.util.NoInternetException
import com.bundlegh.bundle.util.snackbar
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

        binding.textViewForgotPassword.setOnClickListener {
//            Intent(this, HomeActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(it)
//            }
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

        if (email.isNullOrEmpty()) {
            binding.root.snackbar("Please enter your email")
            return
        }

        if (password.isNullOrEmpty()) {
            binding.root.snackbar("Password is required")
            return
        }

        // for fragments use viewLifeCycle.lifeCycleScope
        showProgress()
        Coroutines.io {
            try {
                val authResponse = viewModel.userLogin(email, password)
                if (authResponse.user != null && !authResponse.token.isNullOrEmpty()) {
                    val user = authResponse.user
                    user.token = authResponse.token
                    for (category in authResponse.categories) {
                        viewModel.saveCategory(category)
                    }
                    for (subCategory in authResponse.sub_categories) {
                        viewModel.saveSubCategory(subCategory)
                    }
                    viewModel.saveLoggedInUser(user)
                } else {
                    runOnUiThread{
                        hideProgress()
                        binding.root.snackbar(authResponse.detail)
                    }
                }
            } catch (e: ApiException) {
                runOnUiThread{
                    hideProgress()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: NoInternetException) {
                runOnUiThread{
                    hideProgress()
                    binding.root.snackbar(e.message!!)
                }
            } catch (e: Exception){
                runOnUiThread{
                    hideProgress()
                    binding.root.snackbar("Sorry, an error occurred. Please try again")
                }
            }
        }
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }
}