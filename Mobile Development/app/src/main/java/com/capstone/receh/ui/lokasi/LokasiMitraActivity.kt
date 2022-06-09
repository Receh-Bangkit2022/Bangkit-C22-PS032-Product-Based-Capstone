package com.capstone.receh.ui.lokasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.receh.databinding.ActivityLokasiMitraBinding

class LokasiMitraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLokasiMitraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLokasiMitraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupaction()
    }

    private fun setupaction() {
        binding.backku.setOnClickListener {
            super.onBackPressed()
        }
    }
    }