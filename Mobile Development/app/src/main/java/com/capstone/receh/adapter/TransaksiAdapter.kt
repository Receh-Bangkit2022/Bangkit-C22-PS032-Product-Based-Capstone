package com.capstone.receh.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.receh.databinding.ItemRowTransaksiBinding
import com.capstone.receh.model.API.DetailTransaksi
import com.capstone.receh.model.API.HistoryTransaksiResponse
import com.capstone.receh.ui.ereceipt.EreceiptViewModel
import com.capstone.receh.ui.ereceipt.EreceiptpayActivity
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class TransaksiAdapter : RecyclerView.Adapter<TransaksiAdapter.MyViewHolder>() {

    private var oldTransaksi= emptyList<DetailTransaksi>()
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    fun setData(newTransaksi: List<DetailTransaksi>) {
        val diffUtil = TransaksiDiffUtil(oldTransaksi, newTransaksi)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldTransaksi = newTransaksi
        diffResult.dispatchUpdatesTo(this)
    }

    inner class MyViewHolder(var binding: ItemRowTransaksiBinding): RecyclerView.ViewHolder(binding.root) {
        val calendar = Calendar.getInstance().time
        fun bind(listTransaksi: DetailTransaksi) {
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(listTransaksi)
            }

            binding.apply {
                binding.jenis.text = listTransaksi.jenis
                binding.status.text = listTransaksi.status

                //jumlah uang
                val localeId  = Locale ("id","ID")
                val formatRupiah:NumberFormat=NumberFormat.getCurrencyInstance(localeId)
                val output : TextView =  binding.duit
                val angka = listTransaksi.jumlah
                val uang = formatRupiah.format(angka.toInt())
                output.text = uang

                //tanggal
                val formattgl = DateFormat.getDateInstance(DateFormat.FULL, localeId).format(calendar.time)
                val outputtgl =  tanggal
                val tgl = listTransaksi.tanggal
                val tanggal = formattgl.format(tgl)
                outputtgl.text = tanggal
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(ItemRowTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(oldTransaksi[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(listTransaksi: DetailTransaksi)
    }

    override fun getItemCount(): Int = oldTransaksi.size

}

