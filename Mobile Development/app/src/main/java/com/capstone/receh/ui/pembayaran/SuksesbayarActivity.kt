package com.capstone.receh.ui.pembayaran

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.ActivityWelcome
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity
import com.capstone.receh.databinding.ActivitySuksesTransaksiBinding
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.MainViewModel
import com.capstone.receh.ui.ereceipt.EreceiptTopupActivity
import com.capstone.receh.ui.password.PasswordbayarActivity
import com.capstone.receh.ui.topup.SuksesTopupActivity
import com.capstone.receh.ui.topup.TopupActivity
import java.text.NumberFormat
import java.util.*

class SuksesbayarActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySuksesTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuksesTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        setupaction()
        harga()
    }

    private fun setupaction(){
        binding.backh.setOnClickListener{
           onBackPressed()
        }
        binding.estruk.setOnClickListener{
            val uang = intent.getStringExtra(RUPIAH)
            val seller = intent.getStringExtra(PasswordbayarActivity.USER_ID)
            intent = Intent(this, EreceiptpayActivity::class.java)
            intent.putExtra(EreceiptpayActivity.RUPIAH, uang)
            intent.putExtra(EreceiptpayActivity.USER_ID, seller)
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
    }

    override fun onBackPressed() {
            val home = Intent(applicationContext, MainActivity::class.java)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
    }
}