package com.capstone.receh.ui.password

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.ui.topup.SuksesTopupActivity
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.databinding.ActivityPasswordBinding
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity
import java.text.NumberFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class PasswordtopupActivity : AppCompatActivity() {

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
                    passwordViewModel.login()
                        val intent = Intent(this@PasswordtopupActivity, SuksesTopupActivity::class.java)
                        intent.putExtra(SuksesTopupActivity.RUPIAH, duit)
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
    }
}