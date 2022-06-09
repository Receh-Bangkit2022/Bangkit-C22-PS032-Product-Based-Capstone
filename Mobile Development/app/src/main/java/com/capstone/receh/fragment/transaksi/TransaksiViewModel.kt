package com.capstone.receh.fragment.transaksi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransaksiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tidak ada transaksi saat ini"
    }
    val text: LiveData<String> = _text
}