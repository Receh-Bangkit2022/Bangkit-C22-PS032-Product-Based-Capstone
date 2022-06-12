package com.capstone.receh.ui.topup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.databinding.ActivitySuksesTopupBinding
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity
import java.text.NumberFormat
import java.util.*

class SuksesTopupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuksesTopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuksesTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        harga()
        setupaction()

    }

    private fun harga() {
        val localeId = Locale("id", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
        val output = binding.rupiah
        val duit = intent.getStringExtra(EreceiptpayActivity.RUPIAH)
        val uang = formatRupiah.format(duit?.toDouble())
        output.text = uang
    }

    private fun setupaction() {

        val uang = intent.getStringExtra(RUPIAH)
        val idTransaksi = intent.getStringExtra(IDTRANSAKSI)

        binding.estruk.setOnClickListener {
            intent = Intent(this, EreceiptpayActivity::class.java)
            intent.putExtra(EreceiptpayActivity.RUPIAH, uang)
            intent.putExtra(EreceiptpayActivity.IDTRANSAKSI,idTransaksi)
            startActivity(intent)
        }

        binding.backh.setOnClickListener {
            onBackPressed()
        }

    }
    override fun onBackPressed() {
        val home = Intent(applicationContext, MainActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
        finish()
    }


    companion object {
        const val RUPIAH = "rupiah"
        const val IDTRANSAKSI = "IDTRANSAKSI"
    }
}