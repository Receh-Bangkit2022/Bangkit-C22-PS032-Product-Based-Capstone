package com.capstone.receh.ui.signup

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.ActivityWelcome
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.databinding.ActivityVerifikasiBinding
import com.capstone.receh.model.UserDatabase
import com.capstone.receh.model.UserPreference
import com.capstone.receh.setFullscreen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class VerifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiBinding
    private lateinit var signupViewModel: SignupViewModel

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private var forceResendingToken : PhoneAuthProvider.ForceResendingToken? = null
    private var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private val TAG = "MAIN_TAG"
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon ditunggu")
        progressDialog.setCanceledOnTouchOutside(false)

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(this@VerifikasiActivity, "Kode verifikasi salah, mohon periksa kembali.", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                //Menampilkan status setelah dikirim dan jika ingin resend code OTP
                Toast.makeText(this@VerifikasiActivity, "Kode verifikasi telah kirim...", Toast.LENGTH_SHORT).show()
                binding.codeSentDescriptionTv.visibility = View.VISIBLE
                binding.resendCodeTv.visibility = View.VISIBLE
                binding.codeSentDescriptionTv.text = "Silakan masukkan kode verifikasi yang kami kirim ke +62${binding.phonEt.text.toString().trim()}"

            }
        }

        binding.phoneContinueBtn.setOnClickListener {
            val phone = "+62" + binding.phonEt.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerifikasiActivity, "Mohon masukkan nomor telepon anda", Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }

        binding.resendCodeTv.setOnClickListener {
            val phone = "+62" + binding.phonEt.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerifikasiActivity, "Mohon masukkan nomor telepon anda", Toast.LENGTH_SHORT).show()
            }
            else{
                resendVerificationCode(phone, forceResendingToken)
            }
        }

        binding.codeSubmitBtn.setOnClickListener {
            val code = binding.codeEt.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@VerifikasiActivity, "Silakan masukkan kode verifikasi", Toast.LENGTH_SHORT).show()
            }
            else{
                mVerificationId?.let { it1 -> verifyPhoneNumberWithCode(it1, code) }
            }
        }
        setupViewModel()
    }


    private fun setupViewModel() {
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignupViewModel::class.java]
    }

    private fun startPhoneNumberVerification(phone: String){
        progressDialog.setMessage("Memverifikasi Nomor Telepon...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken?){
        progressDialog.setMessage("Mengirim Ulang Kode...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!)
            .setForceResendingToken(token!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String , code: String){
        progressDialog.setMessage("Memverifikasi Kode...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        progressDialog.setMessage("Masuk")
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                val name = intent.getStringExtra(USER_NAME)
                val email = intent.getStringExtra(USER_EMAIL)
                val password = intent.getStringExtra(USER_PASSWORD)

                firebaseAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser?.uid.toString()
                            val balance = 100000
                            val recehId = "Receh-" + currentUser
                            database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                            val User = UserDatabase(name, email, password, phone, recehId, balance)

                            database.child(recehId).setValue(User).addOnSuccessListener {

                                Toast.makeText(this, "Registrasi Sukses!. ", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, ActivityWelcome::class.java))
                                finish()

                            }.addOnFailureListener {
                                Toast.makeText(this,"Gagal", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_LONG).show()
                        }
                    }

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val USER_NAME = "USER_NAME"
        const val USER_EMAIL = "USER_EMAIL"
        const val USER_PASSWORD = "USER_PASSWORD"
    }
}
