package com.bundlegh.bundle.ui.auth.signup

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bundlegh.bundle.databinding.ActivitySignupBinding
import com.bundlegh.bundle.ui.auth.AuthViewModel
import com.bundlegh.bundle.ui.auth.AuthViewModelFactory
import com.bundlegh.bundle.ui.auth.LoginActivity
import com.bundlegh.bundle.ui.home.HomeActivity
import com.bundlegh.bundle.util.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SignupActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupAdapter: SignupAdapter
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager2 = binding.viewPager2
        buttonNext = binding.buttonNext
        buttonPrevious = binding.buttonPrevious

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.getLoggedInUser().observe(this) { user ->
            if (user != null) {
                Intent(this, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        }
        setupAdapter()
        loadSchools()
        binding.buttonPrevious.setOnClickListener {
            binding.viewPager2.previousPage(true)
            buttonNext.text = "Next"
        }
        binding.buttonNext.setOnClickListener {
            nextPage()
        }
        binding.textViewLogin.setOnClickListener {
            startLoginActivity()
        }
        binding.buttonContinue.setOnClickListener {
            confirmVerify()
        }

    }

    private fun setupAdapter(){
        signupAdapter = SignupAdapter(supportFragmentManager, lifecycle)
        binding.viewPager2.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = signupAdapter
        }
    }

    private fun nextPage(){
        val last = viewPager2.nextPage(true)
        if (buttonNext.text == "Sign me Up"){
//            binding.root.snackbar("Signup clicked")
            userSignup()
        }
        if (!last){
            buttonNext.text = "Sign me Up"
        }
    }

//    private fun getCurrentFragment = supportFragmentManager.findFragmentByTag("f${viewPager2.currentItem}")

    private fun userSignup() {
        val name = (supportFragmentManager.findFragmentByTag("f0") as OneFragment).getName()
        val username = (supportFragmentManager.findFragmentByTag("f0") as OneFragment).getUsername()
        val email = (supportFragmentManager.findFragmentByTag("f1") as TwoFragment).getEmail()
        val school = (supportFragmentManager.findFragmentByTag("f1") as TwoFragment).getSchool()
        val password = (supportFragmentManager.findFragmentByTag("f2") as ThreeFragment).getPassword()
        val passwordConfirm = (supportFragmentManager.findFragmentByTag("f2") as ThreeFragment).getPasswordConfirm()

        // Input validation
        if(name.isNullOrEmpty()){
            binding.rootLayout.snackbar("Name is required")
            return
        }
        if(username.isNullOrEmpty()){
            binding.rootLayout.snackbar("Username is required")
            return
        }
        if(email.isNullOrEmpty()){
            binding.rootLayout.snackbar("Email is required")
            return
        }
        if (school < 0){
            binding.rootLayout.snackbar("School is required")
            loadSchools()
            return
        }
        if(password.isNullOrEmpty()){
            binding.rootLayout.snackbar("Please enter a password")
            return
        }
        if (password != passwordConfirm){
            binding.rootLayout.snackbar("Password does not match")
            return
        }

        Coroutines.io {
            try {
                runOnUiThread { showProgress() }
                val authResponse = viewModel.userSignup(name, username, email, school.toString(), password)
                if (authResponse.isSuccessful == true) {
                    binding.rootLayout.snackbar("Registration Successful\nPlease login to continue")
                    runOnUiThread {
                        binding.root.snackbar(authResponse.detail)
                        hideProgress()
                        showConfirmVerify()
                    }
                } else {
                    runOnUiThread { binding.root.snackbar(authResponse.detail) }
                }
                runOnUiThread { hideProgress() }
                loadSchools()
            } catch (e: ApiException) {
                runOnUiThread {
                    binding.root.snackbar(e.message!!)
                    hideProgress()
                }
                loadSchools()
            } catch (e: NoInternetException) {
                runOnUiThread {
                    binding.root.snackbar(e.message!!)
                    hideProgress()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.root.snackbar(e.message!!)
                    hideProgress()
                }
                loadSchools()
            }
        }
    }

    private fun confirmVerify(){
        startLoginActivity()
    }

    private fun loadSchools() {
        Coroutines.io {
            try {
                val schoolResponse = viewModel.loadSchools()
                for (school in schoolResponse.schools)
                    viewModel.saveSchool(school)
            } catch (e: ApiException) {
//                runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: NoInternetException) {
//                runOnUiThread { binding.root.snackbar(e.message!!) }
            } catch (e: Exception) {
//                runOnUiThread { binding.root.snackbar("Sorry, an error occurred") }
            }
        }
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun showConfirmVerify() {
        binding.confirmVerify.visibility = View.VISIBLE
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}