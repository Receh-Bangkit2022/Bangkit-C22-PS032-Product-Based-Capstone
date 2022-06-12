package com.capstone.receh.ui.pembayaran

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.databinding.ActivitySuksesTransaksiBinding
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity
import com.capstone.receh.ui.password.PasswordbayarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class SuksesbayarActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySuksesTransaksiBinding
    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuksesTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        setupaction()
        harga()
    }

    private fun setupaction(){
        binding.backh.setOnClickListener{
           onBackPressed()
        }
        binding.estruk.setOnClickListener{
            val uang = intent.getStringExtra(RUPIAH)
            val seller = intent.getStringExtra(PasswordbayarActivity.ID_PENERIMA)
            val penjual = intent.getStringExtra(USER_ID)
            val idTransaksi = intent.getStringExtra(IDTRANSAKSI)
            intent = Intent(this, EreceiptpayActivity::class.java)
            intent.putExtra(EreceiptpayActivity.RUPIAH, uang)
            intent.putExtra(EreceiptpayActivity.IDPENERIMA, seller)
            intent.putExtra(EreceiptpayActivity.USER_ID,penjual)
            intent.putExtra(EreceiptpayActivity.IDTRANSAKSI,idTransaksi)
            startActivity(intent)
        }
    }

    private fun harga(){
        val localeId  = Locale ("id","ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
        val output =  binding.rupiah
        val duit = intent.getStringExtra(RUPIAH)
        val uang = formatRupiah.format(duit?.toDouble())
        output.text = uang
    }

    companion object{
        const val RUPIAH = "rupiah"
        const val USER_ID = "user-id"
        const val IDPENERIMA = "IDPENERIMA"
        const val IDTRANSAKSI = "IDTRANSAKSI"
    }

    override fun onBackPressed() {
            val home = Intent(applicationContext, MainActivity::class.java)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
                   }
    }