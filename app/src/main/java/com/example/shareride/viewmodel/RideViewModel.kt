package com.example.shareride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareride.data.Ride
import com.google.firebase.firestore.FirebaseFirestore

class RideViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _rideAdded = MutableLiveData<Boolean>()
    val rideAdded: LiveData<Boolean> get() = _rideAdded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addRideToDatabase(ride: Ride) {
        firestore.collection("rides")
            .add(ride)
            .addOnSuccessListener {
                _rideAdded.value = true
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
            }
    }
}
