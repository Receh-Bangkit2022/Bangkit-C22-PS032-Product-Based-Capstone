package com.capstone.receh.ui.lokasi

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.receh.model.API.ApiConfig
import com.capstone.receh.model.API.ListLocation
import com.capstone.receh.model.API.location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel {
    private val _mitraLocation = MutableLiveData<List<ListLocation>>()
    val mitraLocation: LiveData<List<ListLocation>> = _mitraLocation

    private val _sellerLocation = MutableLiveData<List<ListLocation>>()
    val sellerLocation: LiveData<List<ListLocation>> = _sellerLocation

    fun showMitra(){
        ApiConfig.getApiService().getMitraLocation().enqueue(object : Callback<location>{
            override fun onResponse(call: Call<location>, response: Response<location>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    _mitraLocation.value = responseBody.dataLoc
                }
            }

            override fun onFailure(call: Call<location>, t: Throwable) {
            }
        })
    }

    fun showSeller(){
        ApiConfig.getApiService().getSelleraLocation().enqueue(object : Callback<location>{
            override fun onResponse(call: Call<location>, response: Response<location>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    _mitraLocation.value = responseBody.dataLoc
                }
            }

            override fun onFailure(call: Call<location>, t: Throwable) {

            }
        })
    }

}