package com.example.chatapp.song

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MySongService {
    private val baseUrl = "https://www.googleapis.com/youtube/v3/"
    private val retrofit: Retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(
        GsonConverterFactory
        .create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    fun getRetrofit(): MySongAPI? {
        return retrofit.create(MySongAPI::class.java)
    }
}