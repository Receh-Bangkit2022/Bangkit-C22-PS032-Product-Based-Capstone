package com.capstone.receh.ui.scan

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dce_main)
        overlay = findViewById(R.id.dce_overlay)
        previewView = findViewById(R.id.dce_viewFinder)
        lampu = findViewById(R.id.lampu)
        back = findViewById(R.id.backku)
        setFullscreen(window)
        DarkStatusBar(window)
        flashlight()
        backh()
        try {
            // Get a license key from https://www.dynamsoft.com/customer/license/trialLicense?product=dbr
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
        // image processing
        // Log.i(TAG, "image processing.................");
        var results: Array<TextResult>? = null
        // Rotate 90 degree to get correct bounding box
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(dceFrame.toBitmap(), 0, 0, dceFrame.toBitmap().getWidth(), dceFrame.toBitmap().getHeight(), matrix, true);
        try {
            val settings = reader!!.runtimeSettings
            settings.barcodeFormatIds = EnumBarcodeFormat.BF_QR_CODE
            reader!!.updateRuntimeSettings(settings)
        } catch (e: BarcodeReaderException) {
            e.printStackTrace()
        }
        val start = System.currentTimeMillis()
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

                if(textscan.contains("Receh-")) {
                    val objectIntent = Intent(application, ProsesbayarActivity::class.java)
                    objectIntent.putExtra(ProsesbayarActivity.USER_ID, textscan)
                    startActivity(objectIntent)
                    finish()
                }


                overlay!!.add(BarcodeGraphic(overlay, null, result, isPortrait))
            }
            overlay!!.postInvalidate()
        }}

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
    }
}