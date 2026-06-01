package com.example.serviceflow.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("json/last/{moeda}")
    suspend fun getExchangeRate(@Path("moeda") moeda: String): Response<Map<String, Double>>
}
