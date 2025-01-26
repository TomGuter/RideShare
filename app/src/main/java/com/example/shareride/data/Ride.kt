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
    val longitude: Double = 0.0,
    val ratingSum: Float = 0f,
    val ratingCount: Int = 0,
    val vacantSeats: Int = 0,
    val joinedUsers: ArrayList<String> = arrayListOf()
) {
    constructor() : this(
        name = "",
        driverName = "",
        routeFrom = "",
        routeTo = "",
        date = "",
        departureTime = "",
        rating = 0f,
        userId = "",
        latitude = 0.0,
        longitude = 0.0,
        ratingSum = 0f,
        ratingCount = 0,
        vacantSeats = 0,
        joinedUsers = arrayListOf()
    )
}
