package com.capstone.receh.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.receh.databinding.FragmentHomeBinding
import com.capstone.receh.ui.MainViewModel
import com.capstone.receh.ui.barcode.QRcodeActivity
import com.capstone.receh.ui.lokasi.LokasiMitraActivity
import com.capstone.receh.ui.lokasi.LokasiSellerActivity
import com.capstone.receh.ui.scan.DceActivity
import com.capstone.receh.ui.topup.TopupActivity
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
        setupAction()
        rupiah()
        return root
    }

    private fun setupAction() {
        binding.topup.setOnClickListener {
            val intent = Intent(activity, TopupActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.mitra.setOnClickListener {
            val intent = Intent(activity, LokasiMitraActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.seller.setOnClickListener {
            val intent = Intent(activity, LokasiSellerActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.scan.setOnClickListener {
            val intent = Intent(activity, DceActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.barcode.setOnClickListener{
            val intent = Intent (activity, QRcodeActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun rupiah(){
   val localeId  = Locale ("id","ID")
        val formatRupiah:NumberFormat=NumberFormat.getCurrencyInstance(localeId)
        val output : TextView =  binding.actbal
        val untung : TextView =  binding.bonus
        val angka = 0
        val uang = formatRupiah.format(angka)
        untung.text = uang
        output.text = uang
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}