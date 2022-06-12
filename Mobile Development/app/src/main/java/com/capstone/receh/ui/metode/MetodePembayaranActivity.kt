
package com.capstone.receh.ui.metode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.databinding.ActivityMetodePembayaranBinding
import com.capstone.receh.ui.password.PasswordtopupActivity
import com.capstone.receh.ui.topup.TopupActivity
import java.text.NumberFormat
import java.util.*

class MetodePembayaranActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMetodePembayaranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetodePembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }
    private fun setupAction() {
        val localeId = Locale("id", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
        val output = binding.jumlah
        val duit = intent.getStringExtra(RUPIAH)
        val uang = formatRupiah.format(duit?.toInt())
        output.text = uang

        binding.mitrareceh.setOnClickListener {
            val intent = Intent(this@MetodePembayaranActivity, PasswordtopupActivity::class.java)
            intent.putExtra(PasswordtopupActivity.RUPIAH, duit )
            startActivity(intent)
            finish()
        }

        binding.backku.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val home = Intent(this, TopupActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
        finish()
    }
    companion object{
        const val RUPIAH = "rupiah"
    }

}