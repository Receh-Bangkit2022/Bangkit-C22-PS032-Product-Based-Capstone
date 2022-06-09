package com.capstone.receh.ui.barcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.ViewModelFactory
import com.capstone.receh.databinding.ActivityQrcodeBinding
import com.capstone.receh.model.UserPreference
import com.capstone.receh.setFullscreen
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class QRcodeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityQrcodeBinding
    private lateinit var qrViewModel: QrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullscreen(window)
        DarkStatusBar(window)
        setupaction()
        setupViewModel()

        qrViewModel.getUser().observe(this) {
            val data = it.id

            if (data.isEmpty()) {
                Toast.makeText(this, "Please fill the data", Toast.LENGTH_SHORT).show()
            } else {
                val writer = QRCodeWriter()
                try {

                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bmp.setPixel(
                                x,
                                y,
                                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                            )
                        }
                    }
                    binding.imageImage6.setImageBitmap(bmp)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }


            }
        }
    }

    private fun setupViewModel() {
        qrViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[QrViewModel::class.java]
    }


    private fun setupaction() {
        binding.backku.setOnClickListener {
            super.onBackPressed()
        }
    }

        private fun barcodegen(){

    }
}


