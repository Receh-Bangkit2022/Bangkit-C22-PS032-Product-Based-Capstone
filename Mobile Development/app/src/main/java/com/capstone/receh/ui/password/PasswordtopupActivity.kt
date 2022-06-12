package com.capstone.receh.ui.password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.capstone.receh.ui.metode.MetodePembayaranActivity
import com.capstone.receh.ui.topup.SuksesTopupActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class PasswordtopupActivity : AppCompatActivity() {

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
        val duit = intent.getStringExtra(RUPIAH)
        val uang = formatRupiah.format(duit?.toDouble())
        output.text = uang
        binding.next.setOnClickListener {
            binding.jumlah.text = duit

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
                    val user = "Receh-" + auth.currentUser?.uid.toString()
                    val userreference = databaseReference?.child(user)
                    userreference?.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val oldBalance = snapshot.child("balance").value.toString()
                            val oldBalance2 = oldBalance.toInt()
                            val nominal = duit?.toInt()
                            val newBalance = oldBalance2 + nominal!!
                            userreference.child("balance").setValue(newBalance.toString()).addOnSuccessListener {
                                successTopUp(user,duit)
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
            onBackPressed()
        }
    }

    private fun successTopUp(idUser:String, duit : String){
        ApiConfig.getApiService().addTransaksi(idUser, duit, "Receh", "Top UP")
            .enqueue(object : Callback<TransaksiResponse> {
                override fun onResponse(
                    call: Call<TransaksiResponse>,
                    response: Response<TransaksiResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        idTransaksi = responseBody.data.TransaksiId
                    }
                    val intent = Intent(this@PasswordtopupActivity, SuksesTopupActivity::class.java)
                    intent.putExtra(SuksesTopupActivity.RUPIAH, duit)
                    intent.putExtra(SuksesTopupActivity.IDTRANSAKSI, idTransaksi)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                    Toast.makeText(this@PasswordtopupActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        val duit = intent.getStringExtra(RUPIAH).toString()
        val home = Intent(applicationContext, MetodePembayaranActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        home.putExtra(MetodePembayaranActivity.RUPIAH, duit)
        startActivity(home)
        finish()
    }

    companion object{
        const val RUPIAH = "rupiah"
    }
}