package com.example.shareride.data

data class Ride(
    val name: String = "",
    val driverName: String = "",
    val routeFrom: String = "",
    val routeTo: String = "",
    val date: String = "",
    val departureTime: String = "",
    val rating: Float = 0f,
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

