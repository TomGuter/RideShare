package com.example.shareride.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ride(
    @PrimaryKey val id: String,
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
) {

    companion object {

        private const val ID_KEY = "id"
        private const val NAME_KEY = "name"
        private const val DRIVER_NAME_KEY = "driverName"
        private const val ROUTE_FROM_KEY = "routeFrom"
        private const val ROUTE_TO_KEY = "routeTo"
        private const val DATE_KEY = "date"
        private const val DEPARTURE_TIME_KEY = "departureTime"
        private const val RATING_KEY = "rating"
        private const val USER_ID_KEY = "userId"
        private const val LATITUDE_KEY = "latitude"
        private const val LONGITUDE_KEY = "longitude"

        fun fromJSON(json: Map<String, Any>): Ride {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val driverName = json[DRIVER_NAME_KEY] as? String ?: ""
            val routeFrom = json[ROUTE_FROM_KEY] as? String ?: ""
            val routeTo = json[ROUTE_TO_KEY] as? String ?: ""
            val date = json[DATE_KEY] as? String ?: ""
            val departureTime = json[DEPARTURE_TIME_KEY] as? String ?: ""
            val rating = (json[RATING_KEY] as? Number)?.toFloat() ?: 0f
            val userId = json[USER_ID_KEY] as? String ?: ""
            val latitude = (json[LATITUDE_KEY] as? Number)?.toDouble() ?: 0.0
            val longitude = (json[LONGITUDE_KEY] as? Number)?.toDouble() ?: 0.0

            return Ride(
                id = id,
                name = name,
                driverName = driverName,
                routeFrom = routeFrom,
                routeTo = routeTo,
                date = date,
                departureTime = departureTime,
                rating = rating,
                userId = userId,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                NAME_KEY to name,
                DRIVER_NAME_KEY to driverName,
                ROUTE_FROM_KEY to routeFrom,
                ROUTE_TO_KEY to routeTo,
                DATE_KEY to date,
                DEPARTURE_TIME_KEY to departureTime,
                RATING_KEY to rating,
                USER_ID_KEY to userId,
                LATITUDE_KEY to latitude,
                LONGITUDE_KEY to longitude
            )
        }
}