package com.capstone.receh.ui.pusatbantuan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityPertanyaanBinding

class PertanyaanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPertanyaanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPertanyaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBtn()
    }

    private fun setupBtn(){
        binding.backku.setOnClickListener{
            super.onBackPressed()
        }

        binding.button.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan1) )
            startActivity(intent)
        }
        binding.button2.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan2) )
            startActivity(intent)
        }
        binding.button3.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan3) )
            startActivity(intent)
        }
        binding.button4.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan4) )
            startActivity(intent)
        }
        binding.button5.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan5) )
            startActivity(intent)
        }
        binding.button6.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan6) )
            startActivity(intent)
        }
        binding.button7.setOnClickListener {
            val intent = Intent(this@PertanyaanActivity, JawabanActivity::class.java)
            intent.putExtra(JawabanActivity.EXTRA_TEXT, resources.getString(R.string.pertanyaan7) )
            startActivity(intent)
        }
    }
}