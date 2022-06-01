package com.capstone.receh.signup

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.*
import com.capstone.receh.databinding.ActivityVerifikasiBinding
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class VerifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiBinding
    private lateinit var signupViewModel: SignupViewModel

    //if code sending failed will used to resend
    private var forceResendingToken : PhoneAuthProvider.ForceResendingToken? = null

    private var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(this@VerifikasiActivity, "${e.message}", Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                Log.d(TAG, "onCodeSent: $verificationId")


                //hide phone layout, show code layout
                Toast.makeText(this@VerifikasiActivity, "Verification code sent...", Toast.LENGTH_SHORT).show()
                binding.codeSentDescriptionTv.visibility = View.VISIBLE
                binding.resendCodeTv.visibility = View.VISIBLE
                binding.codeSentDescriptionTv.text = "Please type the verification code we sent to +62${binding.phonEt.text.toString().trim()}"

            }
        }

        binding.phoneContinueBtn.setOnClickListener {
            //input phone number
            val phone = "+62" + binding.phonEt.text.toString().trim()
            //validation
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerifikasiActivity, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }

        binding.resendCodeTv.setOnClickListener {
            //input phone number
            val phone = "+62" + binding.phonEt.text.toString().trim()
            //validation
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerifikasiActivity, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
            else{
                resendVerificationCode(phone, forceResendingToken)
            }
        }

        binding.codeSubmitBtn.setOnClickListener {
            //input verification code
            val code = binding.codeEt.text.toString().trim()
            //validation
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@VerifikasiActivity, "Please enter verification code", Toast.LENGTH_SHORT).show()
            }
            else{
//                verifyPhoneNumberWothCode(mVerificationId, code)
                mVerificationId?.let { it1 -> verifyPhoneNumberWothCode(it1, code) }
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
        Log.d(TAG, "startPhoneNumberVerification: $phone")
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!) ////disini tak tambahin !!
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken?){
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        Log.d(TAG, "resendVerificationCode: +62$phone")

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks!!) ////disini tak tambahin !!
            .setForceResendingToken(token!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWothCode(verificationId: String , code: String){ // tanda tanya di verification tak ilangin
        Log.d(TAG, "verifyPhoneNumberWothCode: $verificationId $code")
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        Log.d(TAG, "signInWithPhoneAuthCredential: ")
        progressDialog.setMessage("Logging In")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this, "Account Has Been Created!", Toast.LENGTH_SHORT).show()


                //start profile act
                val name = intent.getStringExtra(USER_NAME)
                val email = intent.getStringExtra(USER_EMAIL)
                val password = intent.getStringExtra(USER_PASSWORD)

                signupViewModel.saveUser(UserModel(name.toString(), email.toString(), password.toString(), phone.toString(), false))
                startActivity(Intent(this, ActivityWelcome::class.java))
                finish()
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
