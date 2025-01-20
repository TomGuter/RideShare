package com.example.shareride.utils

import com.example.shareride.data.Ride
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun addRide(ride: Ride, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("rides")
            .add(ride)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Unknown error") }
    }
}
