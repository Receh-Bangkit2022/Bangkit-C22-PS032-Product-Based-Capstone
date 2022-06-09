package com.capstone.receh.ui.password

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.R
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.databinding.ActivityPasswordBinding
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.pembayaran.ProsesbayarActivity
import com.capstone.receh.ui.pembayaran.SuksesbayarActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class PasswordbayarActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPasswordBinding
    private lateinit var user: UserModel
    private lateinit var passwordViewModel: PasswordViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val duit = intent.getStringExtra(PasswordtopupActivity.RUPIAH)
        val uang = formatRupiah.format(duit?.toDouble())
        output.text = uang

        val seller = intent.getStringExtra(USER_ID)

        binding.next.setOnClickListener {
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
                    passwordViewModel.login()
                        val intent = Intent(this@PasswordbayarActivity, SuksesbayarActivity::class.java)
                        intent.putExtra(SuksesbayarActivity.RUPIAH, duit )
                        intent.putExtra(SuksesbayarActivity.USER_ID, seller )
                        startActivity(intent)
                    finish()
                }
            }
        }
            binding.backku.setOnClickListener{
                super.onBackPressed()

        }
    }
    companion object{
        const val RUPIAH = "rupiah"
        const val USER_ID = "user-id"
    }
}