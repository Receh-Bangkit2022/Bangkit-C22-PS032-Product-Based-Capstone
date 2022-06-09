package com.capstone.receh.ui.pembayaran

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.databinding.ActivityProsestarikcustBinding
import com.capstone.receh.ui.password.PasswordbayarActivity

class ProsesbayarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProsestarikcustBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProsestarikcustBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hasilnama()
        next()
        home()
    }

        private fun next() {
            binding.next.setOnClickListener {
                val rupiah = binding.rupiahEt.text.toString()
                val seller = binding.seller.text.toString()
                when {
                    rupiah.isEmpty() -> {
                        binding.rupiahEt.error = "Masukkan nominal rupiah"
                    }
                    rupiah.toInt() < 500->{
                        binding.rupiahEt.error = "Minimal belanja Rp 500,- "
                    }
                    else -> {
                        val hasil = Intent(this, PasswordbayarActivity::class.java)
                        hasil.putExtra(PasswordbayarActivity.RUPIAH, rupiah)
                        hasil.putExtra(PasswordbayarActivity.USER_ID, seller)
                        startActivity(hasil)
            }
        }
            }
        }

        private fun hasilnama() {
            val name = intent.getStringExtra(USER_ID)
            binding.seller.text = name
        }


        private fun home() {
            binding.backku.setOnClickListener {
                super.onBackPressed()
            }
        }

        companion object {
            const val USER_ID = "user-id"
        }
    }
