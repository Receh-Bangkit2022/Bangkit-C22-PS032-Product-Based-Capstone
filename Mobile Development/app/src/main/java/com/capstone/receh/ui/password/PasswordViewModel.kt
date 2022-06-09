package com.capstone.receh.ui.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.receh.model.UserModel
import com.capstone.receh.model.UserPreference
import kotlinx.coroutines.launch

class PasswordViewModel(private val pref: UserPreference) : ViewModel() {
    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}