package com.example.chatapp.song

import io.reactivex.Observable
import retrofit2.http.GET

interface MySongAPI {
    @GET("videos")

    fun getAllTracks(): Observable<ArrayList<MySong>>
}