package com.capstone.receh.fragment.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.receh.adapter.TransaksiAdapter
import com.capstone.receh.databinding.FragmentTransaksiBinding
import com.capstone.receh.model.API.DetailTransaksi
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity

class TransaksiFragment : Fragment() {
    private var _binding: FragmentTransaksiBinding? = null
    private lateinit var idUser: String
    private lateinit var listTransaksiAdapter: TransaksiAdapter

    private val viewModel by viewModels<TransaksiViewModel>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val args = arguments
        idUser = args?.getString(idUser).toString()
        listTransaksiAdapter = TransaksiAdapter()
        listTransaksiAdapter.notifyDataSetChanged()

        binding.apply {
            rvTrans.setHasFixedSize(true)
            rvTrans.layoutManager = LinearLayoutManager(activity)
            rvTrans.adapter = listTransaksiAdapter
        }

        viewModel.getListTransaksi(idUser)
        viewModel.listTransaksi.observe(viewLifecycleOwner) {
            listTransaksiAdapter.setData(it)
       }

        listTransaksiAdapter.setOnItemClickCallback(object : TransaksiAdapter.OnItemClickCallback {
            override fun onItemClicked(listTransaksi: DetailTransaksi) {
               Intent(activity, EreceiptpayActivity::class.java).also {
                    it.putExtra(EreceiptpayActivity.USER_ID, listTransaksi.idUser)
                    it.putExtra(EreceiptpayActivity.IDTRANSAKSI, listTransaksi.idTransaksi)
                    startActivity(it)
                }
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}