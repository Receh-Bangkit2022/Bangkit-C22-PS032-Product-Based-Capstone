package com.capstone.receh.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.*
import com.capstone.receh.databinding.ActivitySignUpBinding

import com.capstone.receh.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
setFullscreen(window)
      DarkStatusBar(window)
        setupViewModel()
        setupAction()
    }


    private fun setupViewModel() {
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignupViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnDaftar.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameInput.error = "Masukkan nama"
                }
                checkEmail(email) -> {
                    binding.emailInput.error = "Email yang valid"
                }
                password.length < 8 ->
                    binding.passwordEditText.error = "Gunakan minimal 8 karakter"
                email.isEmpty() -> {
                    binding.emailInput.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Masukkan password"
                }
                else -> {
                    Toast.makeText(this, "Akunnya hampir jadi nih, verifikasi nomor telepon dulu yuk!", Toast.LENGTH_SHORT)
                        .show()
                    val objectIntent =
                        Intent(this@SignupActivity, VerifikasiActivity::class.java)
                    objectIntent.putExtra(VerifikasiActivity.USER_NAME, name)
                    objectIntent.putExtra(VerifikasiActivity.USER_EMAIL, email)
                    objectIntent.putExtra(VerifikasiActivity.USER_PASSWORD, password)
                    startActivity(objectIntent)
                }
            }
        }
    }

    private fun checkEmail(email : String) : Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}