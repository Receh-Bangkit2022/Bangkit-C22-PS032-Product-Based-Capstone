package com.capstone.receh.ui.pembayaran

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.receh.databinding.ActivityProsestarikcustBinding
import com.capstone.receh.ui.MainActivity
import com.capstone.receh.ui.password.PasswordbayarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ProsesbayarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProsestarikcustBinding
    private var userReference: DatabaseReference? = null

    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProsestarikcustBinding.inflate(layoutInflater)
        setContentView(binding.root)
        home()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        loadProfileReceiver()
        next()
    }

        private fun next() {
            binding.next.setOnClickListener {
                val nominalKirim = binding.rupiahEt.text.toString()
                val userId = intent.getStringExtra(USER_ID).toString()

                when {
                    nominalKirim.isEmpty() -> {
                            binding.rupiahEt.error = "Masukkan nominal rupiah"
                        }
                    nominalKirim.toInt() < 500->{
                            binding.rupiahEt.error = "Minimal belanja Rp 500,- "
                        }
                        else -> {

                            val user = auth.currentUser
                            val recehId = "Receh-"+ user?.uid

                            userReference = databaseReference?.child(recehId)
                            userReference?.addListenerForSingleValueEvent(object: ValueEventListener { //Cek database pengirim ( User yang login )
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val name = snapshot.child("name").value.toString()
                                    val userBalance = snapshot.child("balance").value.toString()
                                    if (nominalKirim.toInt() > userBalance.toInt()){
                                        cekSaldo("1", nominalKirim, userId, recehId)
                                    }else{
                                        cekSaldo("2", nominalKirim, userId, recehId)
                                        Log.d("Saldo USerr", "$recehId")
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")

                                }
                            })
                        }
                    }
            }
        }

        private fun cekSaldo(case : String, nominalKirim : String, seller: String, pengirim: String){
            when{
                case == "1" ->{
                    AlertDialog.Builder(this@ProsesbayarActivity).apply {
                        setTitle("Oops")
                        setMessage("Saldomu kurang nih, silahkan Top Up terlebih dahulu!")
                        setPositiveButton("kembali") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                }
                case == "2" ->{
                    Toast.makeText(this,"Oke", Toast.LENGTH_SHORT)
                    val hasil = Intent(this, PasswordbayarActivity::class.java)
                    hasil.putExtra(PasswordbayarActivity.NOMINAL, nominalKirim)
                    hasil.putExtra(PasswordbayarActivity.ID_PENERIMA, seller)
                    hasil.putExtra(PasswordbayarActivity.ID_PENGIRIM, pengirim)
                    startActivity(hasil)
                    finish()
                }
            }
        }

        private fun home() {
            binding.backku.setOnClickListener {
                super.onBackPressed()
                finish()
            }
        }

    private fun loadProfileReceiver() {

        val userId = intent.getStringExtra(USER_ID).toString()
        val userreference = databaseReference?.child(userId)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                binding.seller.text = name
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        userReference = null
    }

    override fun onBackPressed() {
        val home = Intent(applicationContext, MainActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
        finish()
    }

        companion object {
            const val USER_ID = "user-id"
            const val BALANCE_EXTRA = "BALANCE_EXTRA"
        }
    }
