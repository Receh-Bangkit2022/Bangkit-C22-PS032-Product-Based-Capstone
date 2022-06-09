package com.capstone.receh.ui.topup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityTopupBinding
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.metode.MetodePembayaranActivity
import com.capstone.receh.ui.password.PasswordtopupActivity
import com.capstone.receh.ui.pembayaran.SuksesbayarActivity
import java.text.NumberFormat
import java.util.*


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

    /*  binding.rupiahEt.addTextChangedListener(object : TextWatcher {
            var rupiah = binding.rupiahEt.text.toString()
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (!s.toString().equals(rupiah)) {
                    binding.rupiahEt.removeTextChangedListener(this)
                    val replace = s.toString().replace("", "Rp ")
                    if (!replace.isEmpty()) {
                        rupiah = harga.


                    }
                }
            }
        })
    }



       fun harga(number : Double){
            val localeId  = Locale ("id","ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)



            val uang = formatRupiah.format(duit?.toDouble())

        }

     */

        private fun setupaction() {
            binding.backku.setOnClickListener{
                super.onBackPressed()
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
                    }
        }
            }
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
