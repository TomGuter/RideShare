package com.example.shareride.model

import com.example.shareride.base.EmptyCallback
import com.example.shareride.base.RidesCallback

class Model private constructor() {

    private val firebaseModel = FirebaseModel()

    companion object {
        val shared = Model()
    }


    fun getAllRides(callback: RidesCallback) {
        firebaseModel.getAllRides(callback)
    }


    fun addRide(ride: Ride, callback: EmptyCallback) {
        firebaseModel.addRide(ride, callback)
    }

    fun deleteRide(rideId: String, callback: EmptyCallback) {
        firebaseModel.deleteRide(rideId, callback)
    }

    fun updateRide(ride: Ride, callback: EmptyCallback) {
        firebaseModel.updateRide(ride, callback)
    }
}
