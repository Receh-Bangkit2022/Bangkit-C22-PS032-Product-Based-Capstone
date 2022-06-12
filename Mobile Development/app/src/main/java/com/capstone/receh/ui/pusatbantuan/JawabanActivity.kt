package com.capstone.receh.ui.pusatbantuan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.databinding.ActivityJawabanBinding


class JawabanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJawabanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJawabanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isinya = intent.getStringExtra(EXTRA_TEXT).toString()
        binding.tvIsi.text = isinya
        setupBtn()
    }

    private fun setupBtn(){
        binding.backku.setOnClickListener {
            super.onBackPressed()
        }
    }

    companion object{
        const val EXTRA_TEXT = "EXTRA_TEXT"
    }
}