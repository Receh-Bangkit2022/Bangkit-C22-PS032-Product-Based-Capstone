package com.capstone.receh.ui.password


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
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.databinding.ActivityPasswordBinding
import com.capstone.receh.model.API.ApiConfig
import com.capstone.receh.model.API.TransaksiResponse
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.pembayaran.ProsesbayarActivity
import com.capstone.receh.ui.pembayaran.SuksesbayarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class PasswordbayarActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPasswordBinding
    private lateinit var user: UserModel
    private lateinit var passwordViewModel: PasswordViewModel
    private lateinit var idTransaksi : String

    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        loadProfileReceiver()
        setupViewModel()
        setupAction()

    }

    private fun setupViewModel() {
        passwordViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[PasswordViewModel::class.java]

        passwordViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun setupAction() {
        val localeId = Locale("id", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeId)
        val output = binding.jumlah
        val duit = intent.getStringExtra(NOMINAL).toString()
        val nominalTF = formatRupiah.format(duit.toDouble())
        output.text = nominalTF
        val seller = intent.getStringExtra(ID_PENERIMA).toString()
        binding.next.setOnClickListener {
            Log.d("isi variable selleratas", "$seller")
            val password = binding.passwordEditText.text.toString()
            when {
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Masukkan password"
                }
                password.length < 8 ->
                    binding.passwordEditText.error = "Gunakan minimal 8 karakter"
                password != user.password -> {
                    binding.passwordEditText.error = "Password tidak sesuai"
                }
                else -> {
                    val user = intent.getStringExtra(ID_PENGIRIM).toString()
                    val userreference = databaseReference?.child(user)
                    userreference?.addListenerForSingleValueEvent(object: ValueEventListener { //Cek database pengirim ( User yang login )
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val oldBalance = snapshot.child("balance").value.toString()
                            val namaUser = snapshot.child("name").value.toString()
                            val oldBalance2 = oldBalance.toInt()
                            val nominalTF2 = duit.toInt()
                            val newBalance = oldBalance2 - nominalTF2
                            Log.d("Saldo lama pengirim", "$oldBalance2")
                            Log.d("Nominal pengirim", "$nominalTF2")
                            Log.d("Saldo baru pengirim", "$newBalance")
                            userreference.child("balance").setValue(newBalance.toString()).addOnSuccessListener {
                                val userreference2 = databaseReference?.child(seller)
                                Log.d("Seller di pref 2", "$seller")
                                userreference2?.addListenerForSingleValueEvent(object: ValueEventListener{ //Cek database penerima ( User yang terima )
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val oldBalancePenerima = snapshot.child("balance").value.toString()
                                        val namaPihak = snapshot.child("name").value.toString()
                                        val oldBalancePenerima2 = oldBalancePenerima.toInt()
                                        val newBalancePenerima = oldBalancePenerima2 + nominalTF2
                                        Log.d("Saldo lama penerima", "$oldBalancePenerima2")
                                        Log.d("Nominal penerima", "$nominalTF2")
                                        Log.d("Saldo lama penerima", "$newBalancePenerima")
                                        userreference2.child("balance").setValue(newBalancePenerima.toString()).addOnSuccessListener {
                                            addTransaksiAPI(user,seller,namaUser, namaPihak, duit)
                                        }.addOnFailureListener {
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                            }.addOnFailureListener {
                                AlertDialog.Builder(this@PasswordbayarActivity).apply {
                                    setTitle("Oops")
                                    setMessage("Saldomu kurang nih, silahkan Top Up terlebih dahulu!")
                                    setPositiveButton("kembali") { _, _ ->
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")

                        }
                    })
                }
            }
        }
        binding.backku.setOnClickListener{
            super.onBackPressed()
        }
    }

    private fun addTransaksiAPI (idUser: String, idPenerima : String, namaUser : String, namaPenerima : String, nominal : String){
        val penjual = intent.getStringExtra(USER_ID)
        ApiConfig.getApiService().addTransaksi(idUser, nominal, namaPenerima, "Kirim")
            .enqueue(object : Callback<TransaksiResponse> {
                override fun onResponse(
                    call: Call<TransaksiResponse>,
                    response: Response<TransaksiResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            idTransaksi = responseBody.data.TransaksiId
                        }
                    }
                    ApiConfig.getApiService().addTransaksi(idPenerima,nominal, namaUser, "Terima")
                        .enqueue(object : Callback<TransaksiResponse> {
                            override fun onResponse(
                                call: Call<TransaksiResponse>,
                                response: Response<TransaksiResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    if (responseBody != null) {
                                        val intent = Intent(this@PasswordbayarActivity,
                                            SuksesbayarActivity::class.java)
                                        Toast.makeText(this@PasswordbayarActivity,"Transfer telah berhasil!", Toast.LENGTH_SHORT).show()
                                        intent.putExtra(SuksesbayarActivity.RUPIAH, nominal)
                                        intent.putExtra(SuksesbayarActivity.IDPENERIMA, idPenerima)
                                        intent.putExtra(SuksesbayarActivity.USER_ID, penjual)
                                        intent.putExtra(SuksesbayarActivity.IDTRANSAKSI, idTransaksi)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                            }
                        })
                }

                override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                    Toast.makeText(this@PasswordbayarActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun loadProfileReceiver() {  //
        val userId = intent.getStringExtra(ProsesbayarActivity.USER_ID).toString()
        val userreference = databaseReference?.child(userId)
        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onBackPressed() {
        val seller = intent.getStringExtra(ID_PENERIMA).toString()
        val home = Intent(applicationContext, ProsesbayarActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        home.putExtra(ProsesbayarActivity.USER_ID, seller)
        startActivity(home)
        finish()
    }

    companion object{
        const val NOMINAL = "NOMINAL"
        const val ID_PENERIMA = "ID_PENERIMA"
        const val ID_PENGIRIM = "ID_PENGIRIM"
        const val USER_ID = "user-id"
    }
}