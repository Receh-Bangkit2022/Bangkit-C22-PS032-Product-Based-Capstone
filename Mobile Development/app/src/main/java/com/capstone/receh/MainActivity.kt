package com.capstone.receh

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.capstone.receh.databinding.ActivityMainBinding
import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.akun.AkunFragment
import com.capstone.receh.ui.home.HomeFragment
import com.capstone.receh.ui.transaksi.TransaksiFragment
import java.text.NumberFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        statuscolor()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setupViewModel()
        mainViewModel = ViewModelProvider(this).get(mainViewModel::class.java)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_transaksi, R.id.navigation_akun))
        navView.setupWithNavController(navController)
    }
    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) {
            if (it.isLogin) {
              it.name
            } else {
                startActivity(Intent(this, ActivityWelcome::class.java))
                finish()
            }
        }
    }

    private fun statuscolor() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    LightStatusBar(window)
                }
                R.id.navigation_transaksi -> {
                    LightStatusBar(window)
                }
                R.id.navigation_akun -> {
                    DarkStatusBar(window)
                }
            }
        }
    }
}