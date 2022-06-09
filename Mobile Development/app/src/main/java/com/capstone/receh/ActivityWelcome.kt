package com.capstone.receh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.databinding.ActivityWelcomePageBinding
import com.capstone.receh.ui.masuk.LoginActivity
import com.capstone.receh.ui.signup.SignupActivity

class ActivityWelcome: AppCompatActivity() {

    private lateinit var binding: ActivityWelcomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        setupAction()
    }

    private fun setupAction() {
        binding.btnMasuk.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}