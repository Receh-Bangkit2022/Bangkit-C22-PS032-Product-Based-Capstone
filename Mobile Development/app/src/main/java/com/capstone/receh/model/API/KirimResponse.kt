package com.capstone.receh.model.API

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransaksiResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: IdTransaksi
) : Parcelable


@Parcelize
data class IdTransaksi(
    val TransaksiId: String
):Parcelable

@Parcelize
data class HistoryTransaksiResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val data: List<DetailTransaksi>
) : Parcelable

@Parcelize
data class DetailTransaksi(
    val idTransaksi: String,
    val idUser: String,
    val pihak: String,
    val jumlah: String,
    val jenis: String,
    val tanggal: String,
    val waktu: String,
    val status: String
):Parcelable

data class location(

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val dataLoc: List<ListLocation>
)

data class ListLocation(
    @field:SerializedName("jenis")
    val jenis: String,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("lat")
    val lat: String,

    @field:SerializedName("lon")
    val lon: String
)

