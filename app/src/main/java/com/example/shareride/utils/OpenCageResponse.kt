package com.example.shareride.utils

import com.google.gson.annotations.SerializedName

data class OpenCageResponse(
    @SerializedName("results") val results: List<Result>
)

data class Result(
    @SerializedName("geometry") val geometry: Geometry
)

data class Geometry(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double
)
