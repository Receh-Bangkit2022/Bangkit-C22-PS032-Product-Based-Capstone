package com.capstone.receh.ui.topup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityTopupBinding
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.metode.MetodePembayaranActivity
class TopupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup)
        binding = ActivityTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        setupaction()
        nominal()}

    private fun setupaction() {
        binding.backku.setOnClickListener{
            onBackPressed()
        }

        binding.next.setOnClickListener {
            val rupiah = binding.rupiahEt.text.toString()

            when {
                rupiah.isEmpty() -> {
                    binding.rupiahEt.error = "Masukkan nominal rupiah"
                }
                rupiah.toInt() < 500->{
                    binding.rupiahEt.error = "Minimal belanja Rp 500,- "
                }
                else -> {
                    val hasil = Intent(this, MetodePembayaranActivity::class.java)
                    hasil.putExtra(MetodePembayaranActivity.RUPIAH, rupiah)
                    startActivity(hasil)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun nominal(){
        binding.opsi1.setOnClickListener{
            binding.rupiahEt.setText(""+10000)
        }
        binding.opsi2.setOnClickListener{
            binding.rupiahEt.setText(""+25000)
        }
        binding.opsi3.setOnClickListener{
            binding.rupiahEt.setText(""+50000)
        }
        binding.opsi4.setOnClickListener{
            binding.rupiahEt.setText(""+100000)
        }
        binding.opsi5.setOnClickListener{
            binding.rupiahEt.setText(""+200000)
        }
        binding.opsi6.setOnClickListener{
            binding.rupiahEt.setText(""+500000)
        }
    }
}
