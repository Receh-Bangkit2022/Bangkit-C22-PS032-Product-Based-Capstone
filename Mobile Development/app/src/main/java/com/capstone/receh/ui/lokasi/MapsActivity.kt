package com.capstone.receh.ui.lokasi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.capstone.receh.R
import com.capstone.receh.databinding.ActivityMapsBinding
import com.capstone.receh.model.API.ApiConfig
import com.capstone.receh.model.API.location
import com.capstone.receh.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setCameraMovement()
        getMyLocation()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val case = intent.getStringExtra(EXTRA_OPTION).toString()
        validasi(case,mMap)
    }

    private fun validasi(case : String, mMap: GoogleMap){
        when{
            case == "mitra" ->{
                supportActionBar?.title = "Lokasi Mitra"
                ApiConfig.getApiService().getMitraLocation().enqueue(object : Callback<location>{
                    override fun onResponse(call: Call<location>, response: Response<location>) {
                        val responseBody = response.body()
                        if (response.isSuccessful && responseBody != null){
                            val locationData = responseBody.dataLoc
                            for (data in locationData) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(data.lat.toDouble(), data.lon.toDouble()))
                                        .title("Nama : "+ data.nama)
                                        .snippet("Jenis : "+data.jenis)
                                        .icon(vectorToBitmap(R.drawable.ic_mitra, Color.parseColor("#E27903")))
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<location>, t: Throwable) {
                        Toast.makeText(this@MapsActivity,"$t", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            case == "seller" ->{
                supportActionBar?.title = "Lokasi Seller"
                ApiConfig.getApiService().getSelleraLocation().enqueue(object : Callback<location>{
                    override fun onResponse(call: Call<location>, response: Response<location>) {
                        val responseBody = response.body()
                        if (response.isSuccessful && responseBody != null){
                            val locationData = responseBody.dataLoc
                            for (data in locationData) {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(data.lat.toDouble(), data.lon.toDouble()))
                                        .title("Nama: "+ data.nama)
                                        .snippet("Jenis : "+data.jenis)
                                        .icon(vectorToBitmap(R.drawable.ic_seller, Color.parseColor("#E27903")))
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<location>, t: Throwable) {
                        Toast.makeText(this@MapsActivity,"$t", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setCameraMovement() {
        val cameraFocus = LatLng(	-8.409518, 115.188919)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraFocus, 5f))
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onBackPressed() {
        val home = Intent(applicationContext, MainActivity::class.java)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
        finish()
    }

    companion object{
        const val EXTRA_OPTION = "EXTRA_OPTION"
    }

}
