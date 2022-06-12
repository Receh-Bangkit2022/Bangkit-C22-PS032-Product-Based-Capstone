package com.capstone.receh.fragment.transaksi

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.receh.model.API.ApiConfig
import com.capstone.receh.model.API.DetailTransaksi
import com.capstone.receh.model.API.HistoryTransaksiResponse
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransaksiViewModel : ViewModel() {
    private var _listTransaksi = MutableLiveData<List<DetailTransaksi>>()
    val listTransaksi : LiveData<List<DetailTransaksi>> = _listTransaksi
    private lateinit var auth: FirebaseAuth
    @SuppressLint("StaticFieldLeak")

    companion object{
        private const val TAG = "TransaksiViewModel"
        private const val idUser = "idUser"
            private const val NOTIFICATION_ID = 1
            private const val CHANNEL_ID = "channel_01"
            private const val CHANNEL_NAME = "dicoding channel"
    }

    init {
        getListTransaksi(idUser)
    }

    fun getListTransaksi(idUser: String) {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        var notifjumlah = 1

        ApiConfig.getApiService()
            .getUserTransaksi("Receh-"+ user?.uid)
            .enqueue(object: Callback<HistoryTransaksiResponse> {
                override fun onResponse(
                    call: Call<HistoryTransaksiResponse>,
                    response: Response<HistoryTransaksiResponse>
                ) {
                    if(response.isSuccessful) {
                        _listTransaksi.postValue(response.body()?.data)
                        notifjumlah += 1
                    }
                }
                override fun onFailure(call: Call<HistoryTransaksiResponse>, t: Throwable) {
                    Log.e("jaringan sibuk", t.message.toString())
                }
            })
    }
}


