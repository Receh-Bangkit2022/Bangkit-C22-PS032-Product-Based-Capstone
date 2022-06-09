package com.capstone.receh.ui.pembayaran

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityGagalTransaksiBinding
import com.capstone.receh.databinding.ActivitySuksesTransaksiBinding
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.MainActivity

class GagalbayarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGagalTransaksiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGagalTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        home()
    }

    private fun home(){
        binding.backh.setOnClickListener{
            val home = Intent(applicationContext, MainActivity::class.java)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
        }
    }

}