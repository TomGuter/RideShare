package com.example.shareride.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCageApiService {
    @GET("geocode/v1/json")
    fun getCoordinates(
        @Query("q") address: String,
        @Query("key") apiKey: String
    ): Call<OpenCageResponse>
}
