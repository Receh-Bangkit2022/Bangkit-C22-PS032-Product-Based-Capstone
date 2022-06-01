package com.capstone.receh.ui.akun

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.receh.model.UserPreference
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AkunViewModel(private val pref: UserPreference) : ViewModel() {
    private lateinit var foto: AkunFragment
    private val _text = MutableLiveData<String>().apply {
        value = "ini akun saya"
    }
    val text: LiveData<String> = _text

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}