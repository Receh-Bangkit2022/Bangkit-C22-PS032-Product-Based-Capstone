package com.capstone.receh

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("CustomSplashScreen")
class Splashscreenactivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setupViewModel()
        Handler(Looper.getMainLooper()).postDelayed({
            mainViewModel.getUser().observe(this) {

                if (it.name.isEmpty()) {
                    startActivity(Intent(this@Splashscreenactivity,
                        ActivityWelcome::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@Splashscreenactivity, MainActivity::class.java))
                    finish()
                }
            }
        }, 2000)
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]
    }
}