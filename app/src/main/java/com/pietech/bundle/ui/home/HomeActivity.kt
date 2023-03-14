package com.pietech.bundle.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.pietech.bundle.R
import com.pietech.bundle.databinding.ActivityHomeBinding
import com.pietech.bundle.ui.home.home.HomeFragmentDirections
import com.pietech.bundle.ui.home.profile.ProfileFragmentDirections
import com.pietech.bundle.util.snackbar
import com.pietech.bundle.util.toast
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        navController = findNavController(R.id.nav_fragment)
        val appBarConfiguration = AppBarConfiguration
            .Builder(R.id.homeFragment, R.id.searchFragment, R.id.cartFragment, R.id.profileFragment)
            .build()
        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_nav, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_notifications -> {
            true
        }

        R.id.action_refresh -> {
            true
        }

        R.id.action_logout -> {
            binding.root.snackbar("log out")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_fragment).navigateUp() || super.onNavigateUp()

    fun setBottomNavigationVisibility(visibility: Int){
        binding.bottomNavigationView.visibility = visibility
    }

    fun navigateToCart(){
        val item = bottomNavigationView.menu.findItem(R.id.cartFragment)
        NavigationUI.onNavDestinationSelected(item, navController)
    }
    fun navigateToProfile(){
        val item = bottomNavigationView.menu.findItem(R.id.profileFragment)
        NavigationUI.onNavDestinationSelected(item, navController)
    }

}



















