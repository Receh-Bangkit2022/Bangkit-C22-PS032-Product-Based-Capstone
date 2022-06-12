package com.capstone.receh.model.API

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("mitra")
    fun getMitraLocation(
    ): Call<location>

    @GET("seller")
    fun getSelleraLocation(
    ): Call<location>

    @FormUrlEncoded
    @POST("transaksi")
    fun addTransaksi(
        @Field ("idUser") idUser : String,
        @Field ("jumlah") jumlah : String,
        @Field ("pihak") pihak : String,
        @Field ("jenis") jenis : String
    ):Call<TransaksiResponse>

    @GET("transaksi/{idUser}")
    fun getUserTransaksi(
        @Path ("idUser") idUser : String
    ):Call<HistoryTransaksiResponse>

    @GET("transaksi/{idUser}/{idTransaksi}")
    fun getDetailTransaksi(
        @Path ("idUser") idUser : String,
        @Path ("idTransaksi") idTransaksi : String
    ):    Call<DetailResponse>

}