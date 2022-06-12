package com.capstone.receh.ui.ereceipt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.databinding.ActivityEreceiptpayBinding
import com.capstone.receh.setFullscreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class EreceiptpayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEreceiptpayBinding
    private val viewModel by viewModels<EreceiptViewModel>()
    private lateinit var idUser: String
    private lateinit var idTransaksi : String

lateinit var auth: FirebaseAuth
 var databaseReference :  DatabaseReference? = null
 var database: FirebaseDatabase? = null

    companion object {
        const val RUPIAH = "rupiah"
        const val USER_ID = "user-id"
        const val IDPENERIMA = "IDPENERIMA"
        const val IDTRANSAKSI = "IDTRANSAKSI"
    }
 override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     binding = ActivityEreceiptpayBinding.inflate(layoutInflater)
     setContentView(binding.root)
     setFullscreen(window)
     DarkStatusBar(window)
     auth = FirebaseAuth.getInstance()
     database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
     databaseReference = database?.reference!!.child("Users")
     setupaction()

     idUser = intent.getStringExtra(USER_ID).toString()
     val bundle = Bundle()
     bundle.putString(USER_ID, idUser)

     idTransaksi = intent.getStringExtra(IDTRANSAKSI).toString()

     viewModel.getDetailTransaksi(idUser, idTransaksi)

     viewModel.listTransaksi.observe(this) {
         if (it != null) {
             binding.apply {
                 seller.text = it.pihak
                 jenis.text = it.jenis + " Sukses"
                 val localeId = Locale("id", "ID")
                 val calendar = Calendar.getInstance().time
                 //jumlah uang transaksi
                 val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
                 val output = rupiah
                 val duit = it.jumlah
                 val uang = formatRupiah.format(duit.toDouble())
                 output.text = uang

                 //waktu transaksi
                 val formatjam = SimpleDateFormat("HH:mm", localeId).format(calendar.time)
                 val outputjam =  jam
                 val jam = it.waktu
                 val waktu = formatjam.format(jam)
                 outputjam.text = waktu

                 //tanggal transaksi
                 val formattgl = DateFormat.getDateInstance(DateFormat.FULL, localeId).format(calendar.time)
                 val outputtgl =  tanggal
                 val tgl = it.tanggal
                 val tanggal = formattgl.format(tgl)
                 outputtgl.text = tanggal
             }
         }
     }
 }
    private fun setupaction() {
     binding.backku.setOnClickListener {
         super.onBackPressed()
     }
 }
}