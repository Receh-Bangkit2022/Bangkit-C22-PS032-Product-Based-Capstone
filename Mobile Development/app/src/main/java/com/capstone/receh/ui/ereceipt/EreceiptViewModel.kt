package com.capstone.receh.ui.ereceipt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.receh.model.API.*
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class EreceiptViewModel : ViewModel() {

    private var _listTransaksi = MutableLiveData<Data>()
    val listTransaksi : LiveData<Data> = _listTransaksi
    private lateinit var auth: FirebaseAuth

    companion object{
        private const val TAG = "TransaksiViewModel"
        private const val idUser = "idUser"
        private const val idTransaksi = "idTransaksi"
    }

    init {
        getDetailTransaksi(idUser, idTransaksi)
    }

        fun getDetailTransaksi(idUser: String, idTransaksi: String) {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        ApiConfig.getApiService()
            .getDetailTransaksi("Receh-"+ user?.uid, idTransaksi)
            .enqueue(object: Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
                ) {
                    val responseBody = response.body()

                    if(response.isSuccessful && responseBody != null) {
                        _listTransaksi.value = responseBody.data
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    Log.e("jaringan sibuk", t.message.toString())
                }
            })
    }
}