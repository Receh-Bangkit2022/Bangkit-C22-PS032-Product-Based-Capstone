package com.capstone.receh.ui.scan

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.receh.DarkStatusBar
import com.capstone.receh.R
import com.capstone.receh.setFullscreen
import com.capstone.receh.ui.ml.BarcodeGraphic
import com.capstone.receh.ui.ml.GraphicOverlay
import com.capstone.receh.ui.pembayaran.ProsesbayarActivity
import com.dynamsoft.dbr.BarcodeReader
import com.dynamsoft.dbr.BarcodeReaderException
import com.dynamsoft.dbr.EnumBarcodeFormat
import com.dynamsoft.dbr.TextResult
import com.dynamsoft.dce.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DceActivity : AppCompatActivity(), DCEFrameListener {
    private var previewView: DCECameraView? = null
    private var lampu : ImageButton? = null
    private var back : ImageButton? = null
    private var cameraEnhancer: CameraEnhancer? = null
    private var reader: BarcodeReader? = null
    private var overlay: GraphicOverlay? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var isPortrait = true
    var isflash = false
    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dce_main)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        overlay = findViewById(R.id.dce_overlay)
        previewView = findViewById(R.id.dce_viewFinder)
        lampu = findViewById(R.id.lampu)
        back = findViewById(R.id.backku)
        setFullscreen(window)
        DarkStatusBar(window)
        flashlight()
        backh()
        try {
            BarcodeReader.initLicense(
                "DLS2eyJoYW5kc2hha2VDb2RlIjoiMTAxMTE2NDY5LVRYbE5iMkpwYkdWUWNtOXEiLCJvcmdhbml6YXRpb25JRCI6IjEwMTExNjQ2OSJ9"
            ) { isSuccessful, e -> }
            reader = BarcodeReader()
        } catch (e: BarcodeReaderException) {
            e.printStackTrace()
        }
        cameraEnhancer = CameraEnhancer(this)
        cameraEnhancer!!.cameraView = previewView
        try {
            cameraEnhancer!!.setResolution(EnumResolution.RESOLUTION_480P)
        } catch (e: CameraEnhancerException) {
            e.printStackTrace()
        }
        cameraEnhancer!!.addListener(this)
        needUpdateGraphicOverlayImageSourceInfo = true
    }

    override fun onResume() {
        super.onResume()
        val orientation = resources.configuration.orientation
        isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        needUpdateGraphicOverlayImageSourceInfo = true
        try {
            cameraEnhancer!!.open()
        } catch (e: CameraEnhancerException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            cameraEnhancer!!.close()
        } catch (e: CameraEnhancerException) {
            e.printStackTrace()
        }
    }

    override fun frameOutputCallback(dceFrame: DCEFrame, l: Long) {
        if (needUpdateGraphicOverlayImageSourceInfo) {
            if (isPortrait) {
                overlay!!.setImageSourceInfo(
                    dceFrame.toBitmap().height, dceFrame.toBitmap().width, false)
            } else {
                overlay!!.setImageSourceInfo(
                    dceFrame.toBitmap().width, dceFrame.toBitmap().height, false)
            }
            needUpdateGraphicOverlayImageSourceInfo = false

        }

        var results: Array<TextResult>? = null
       try {
            val settings = reader!!.runtimeSettings
            settings.barcodeFormatIds = EnumBarcodeFormat.BF_QR_CODE
            reader!!.updateRuntimeSettings(settings)
        } catch (e: BarcodeReaderException) {
            e.printStackTrace()
        }
        try {
            results = reader!!.decodeBufferedImage(dceFrame.toBitmap(), "")
        } catch (e: BarcodeReaderException) {
            e.printStackTrace()
        }
        overlay!!.clear()
        if (results != null && results.size > 0) {
            for (i in results.indices) {
                val result = results[i]
                var textscan = result.barcodeText
                if(textscan.contains("Receh-")){
                    val uid = intent.getStringExtra(UID_EXTRA)
                    if (textscan == uid){
                        Log.d("Cek user", "$uid")
                        AlertDialog.Builder(this).apply {
                            setTitle("Maaf")
                            setMessage("Tidak dapat transfer ke QR sendiri~")
                            setPositiveButton("kembali") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    else {
                        val userreference = databaseReference?.child(textscan)
                        userreference?.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    intentFinish(textscan)
                                }
                                else{
                                    AlertDialog.Builder(this@DceActivity).apply {
                                        setTitle("Maaf")
                                        setMessage("User tidak ditemukan!")
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
                overlay!!.add(BarcodeGraphic(overlay, null, result, isPortrait))
            }
            overlay!!.postInvalidate()
        }}

    private fun intentFinish(textscan : String){
        val userBalance = intent.getStringExtra(BALANCE_EXTRA)
        Log.d("Saldo user", "$userBalance")
        val objectIntent = Intent(this, ProsesbayarActivity::class.java)
        objectIntent.putExtra(ProsesbayarActivity.BALANCE_EXTRA, userBalance)
        objectIntent.putExtra(ProsesbayarActivity.USER_ID, textscan)
        startActivity(objectIntent)
        finish()
    }

    private fun flashlight() {
        lampu?.setOnClickListener {
            isflash = !isflash

            if (isflash){
                cameraEnhancer?.turnOnTorch()
            }else{
                cameraEnhancer?.turnOffTorch()
            }
        }
    }

    private fun backh(){
        back?.setOnClickListener{
            super.onBackPressed()}
    }
    companion object {
        const val TAG = "DCE"
        const val BALANCE_EXTRA = "BALANCE_EXTRA"
        const val UID_EXTRA = "UID_EXTRA"
    }
}