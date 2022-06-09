package com.capstone.receh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.ui.masuk.LoginViewModel

import com.capstone.receh.model.UserPreference
import com.capstone.receh.ui.signup.SignupViewModel
import com.capstone.receh.fragment.akun.AkunViewModel
import com.capstone.receh.fragment.home.HomeViewModel
import com.capstone.receh.ui.MainViewModel
import com.capstone.receh.ui.barcode.QrViewModel
import com.capstone.receh.ui.password.PasswordViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PasswordViewModel::class.java) -> {
                PasswordViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AkunViewModel::class.java) -> {
                AkunViewModel(pref) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(QrViewModel::class.java) -> {
                QrViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}