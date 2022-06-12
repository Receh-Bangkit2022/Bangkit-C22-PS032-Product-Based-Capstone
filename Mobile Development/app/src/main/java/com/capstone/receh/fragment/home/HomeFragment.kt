package com.capstone.receh.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.capstone.receh.databinding.FragmentHomeBinding
import com.capstone.receh.ui.barcode.QRcodeActivity
import com.capstone.receh.ui.lokasi.MapsActivity
import com.capstone.receh.ui.pusatbantuan.PertanyaanActivity
import com.capstone.receh.ui.scan.DceActivity
import com.capstone.receh.ui.topup.TopupActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var auth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://receh-app-v1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database?.reference!!.child("Users")
        loadProfile()
        return root
    }

    private fun setupAction(userBalance : String, userId : String) {
        binding.topup.setOnClickListener {
            val intent = Intent(activity, TopupActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.mitra.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra(MapsActivity.EXTRA_OPTION, "mitra")
            activity?.startActivity(intent)
        }

        binding.seller.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra(MapsActivity.EXTRA_OPTION, "seller")
            activity?.startActivity(intent)
        }

        binding.scan.setOnClickListener {
            val intent = Intent(activity, DceActivity::class.java)
            intent.putExtra(DceActivity.BALANCE_EXTRA, userBalance)
            intent.putExtra(DceActivity.UID_EXTRA, userId)
            activity?.startActivity(intent)
        }

        binding.barcode.setOnClickListener{
            val intent = Intent (activity, QRcodeActivity::class.java)
            activity?.startActivity(intent)
        }
        binding.qna.setOnClickListener{
            val intent = Intent (activity, PertanyaanActivity::class.java)
            activity?.startActivity(intent)
        }
    }



    private fun rupiah(angka : Int){
        val localeId  = Locale ("id","ID")
        val formatRupiah:NumberFormat=NumberFormat.getCurrencyInstance(localeId)
        val output : TextView =  binding.actbal
        val untung : TextView =  binding.bonus
        val uang = formatRupiah.format(angka)
        untung.text = "Segera"
        output.text = uang
    }

    private fun loadProfile() {
        val user = auth.currentUser
        val recehId = "Receh-"+ user?.uid
        val userreference = databaseReference?.child(recehId)

        userreference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val balance = snapshot.child("balance").value.toString()
                val ownId = snapshot.child("id").value.toString()
                val balance2 = balance.toInt()
                rupiah(balance2)
                setupAction(balance,ownId)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object {
        const val TAG = "DCE"
    }

}