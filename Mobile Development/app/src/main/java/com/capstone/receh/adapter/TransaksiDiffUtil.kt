package com.capstone.receh.adapter

import androidx.recyclerview.widget.DiffUtil
import com.capstone.receh.model.API.DetailTransaksi

class TransaksiDiffUtil (
    private val oldList: List<DetailTransaksi>,
    private val newList: List<DetailTransaksi>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].idUser == newList[newItemPosition].idUser

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}