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
    val ratingSum: Float = 0f,
    val ratingCount: Int = 0,
    val rating: Float = if (ratingCount == 0) 0f else String.format("%.2f", ratingSum / ratingCount).toFloat(),
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val vacantSeats: Int = 0,
    val joinedUsers: List<String> = emptyList()
) {

    companion object {

        private const val ID_KEY = "id"
        private const val NAME_KEY = "name"
        private const val DRIVER_NAME_KEY = "driverName"
        private const val ROUTE_FROM_KEY = "routeFrom"
        private const val ROUTE_TO_KEY = "routeTo"
        private const val DATE_KEY = "date"
        private const val DEPARTURE_TIME_KEY = "departureTime"
        private const val RATING_SUM_KEY = "ratingSum"
        private const val RATING_COUNT_KEY = "ratingCount"
        private const val RATING_KEY = "rating"
        private const val USER_ID_KEY = "userId"
        private const val LATITUDE_KEY = "latitude"
        private const val LONGITUDE_KEY = "longitude"
        private const val VACANT_SEATS_KEY = "vacantSeats"
        private const val JOINED_USERS_KEY = "joinedUsers"

        fun fromJSON(json: Map<String, Any>): Ride {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val driverName = json[DRIVER_NAME_KEY] as? String ?: ""
            val routeFrom = json[ROUTE_FROM_KEY] as? String ?: ""
            val routeTo = json[ROUTE_TO_KEY] as? String ?: ""
            val date = json[DATE_KEY] as? String ?: ""
            val departureTime = json[DEPARTURE_TIME_KEY] as? String ?: ""
            val ratingSum = (json[RATING_SUM_KEY] as? Number)?.toFloat() ?: 0f
            val ratingCount = (json[RATING_COUNT_KEY] as? Number)?.toInt() ?: 0
            val rating = if (ratingCount == 0) 0f else String.format("%.2f", ratingSum / ratingCount).toFloat()
            val userId = json[USER_ID_KEY] as? String ?: ""
            val latitude = (json[LATITUDE_KEY] as? Number)?.toDouble() ?: 0.0
            val longitude = (json[LONGITUDE_KEY] as? Number)?.toDouble() ?: 0.0
            val vacantSeats = (json[VACANT_SEATS_KEY] as? Number)?.toInt() ?: 0
            val joinedUsers = (json[JOINED_USERS_KEY] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()

            return Ride(
                id = id,
                name = name,
                driverName = driverName,
                routeFrom = routeFrom,
                routeTo = routeTo,
                date = date,
                departureTime = departureTime,
                ratingSum = ratingSum,
                ratingCount = ratingCount,
                rating = rating,
                userId = userId,
                latitude = latitude,
                longitude = longitude,
                vacantSeats = vacantSeats,
                joinedUsers = joinedUsers
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
                RATING_SUM_KEY to ratingSum,
                RATING_COUNT_KEY to ratingCount,
                RATING_KEY to rating,
                USER_ID_KEY to userId,
                LATITUDE_KEY to latitude,
                LONGITUDE_KEY to longitude,
                VACANT_SEATS_KEY to vacantSeats,
                JOINED_USERS_KEY to joinedUsers
            )
        }
}