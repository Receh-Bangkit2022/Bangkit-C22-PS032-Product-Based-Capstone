package com.capstone.receh.model.API

import com.google.gson.annotations.SerializedName

data class DetailResponse(
	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("status")
	val status: String
)

data class Data(

	@field:SerializedName("idUser")
	val idUser: String,

	@field:SerializedName("pihak")
	val pihak: String,

	@field:SerializedName("jumlah")
	val jumlah: String,

	@field:SerializedName("idTransaksi")
	val idTransaksi: String,

	@field:SerializedName("jenis")
	val jenis: String,

	@field:SerializedName("waktu")
	val waktu: String,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("status")
	val status: String
)
