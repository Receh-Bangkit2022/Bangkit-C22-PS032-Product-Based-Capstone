package com.capstone.receh.ui.lokasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.receh.databinding.ActivityLokasiMitraBinding
import com.capstone.receh.databinding.ActivityLokasiSellerBinding

class LokasiSellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLokasiSellerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLokasiSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupaction()
    }

    private fun setupaction() {
        binding.backku.setOnClickListener {
            super.onBackPressed()
        }
    }
}