package com.capstone.receh.ui.ereceipt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.databinding.ActivityEreceiptpayBinding
import com.capstone.receh.setFullscreen
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class EreceiptpayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEreceiptpayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEreceiptpayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        setupaction()
        harga()
        seller()
        savedata()
    }

    private fun setupaction() {
        binding.backku.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun savedata(){
        val calendar = Calendar.getInstance().time
        binding.jam.text = SimpleDateFormat("HH:mm", Locale.US).format(calendar.time)
        binding.tanggal.text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).format(calendar.time)
    }

    private fun harga() {
        val localeId = Locale("id", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
        val output = binding.rupiah
        val duit = intent.getStringExtra(RUPIAH)
        val uang = formatRupiah.format(duit?.toDouble())
        output.text = uang
    }

    private fun seller(){
        val seller = intent.getStringExtra(USER_ID)
        binding.seller.text = seller
    }

    companion object {
        const val RUPIAH = "rupiah"
        const val USER_ID = "user-id"
    }
}