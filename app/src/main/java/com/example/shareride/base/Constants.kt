package com.example.shareride.base

import com.example.shareride.model.Ride

typealias RidesCallback = (List<Ride>) -> Unit
typealias EmptyCallback = () -> Unit

object Constants {

    object COLLECTIONS {
        const val RIDES = "rides" // Firestore collection for rides
    }
}
