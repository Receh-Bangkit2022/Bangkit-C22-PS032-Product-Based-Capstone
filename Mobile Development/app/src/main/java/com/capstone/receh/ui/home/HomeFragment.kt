package com.capstone.receh.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.receh.*
import com.capstone.receh.databinding.ActivityScanBarcodeBinding
import com.capstone.receh.databinding.ActivityWelcomePageBinding
import com.capstone.receh.databinding.FragmentHomeBinding
import com.capstone.receh.masuk.LoginActivity
import com.capstone.receh.model.UserPreference
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var akunViewModel : MainViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        scan()
        rupiah()
        return root
    }


    private fun scan(){
        binding.scan.setOnClickListener{
            val intent = Intent (activity, scan_barcode::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun rupiah(){
        val localeId  = Locale ("id","ID")
        val formatRupiah:NumberFormat=NumberFormat.getCurrencyInstance(localeId)
        val output : TextView =  binding.actbal
        val untung : TextView =  binding.bonus
        val angka = 0
        val uang = formatRupiah.format(angka.toDouble())
        untung.text = uang
        output.text = uang
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}