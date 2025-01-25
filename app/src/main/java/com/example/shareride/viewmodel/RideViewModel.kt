package com.example.shareride.viewmodel

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareride.model.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RideViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _rideAdded = MutableLiveData<Boolean>()
    val rideAdded: LiveData<Boolean> get() = _rideAdded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _userRides = MutableLiveData<List<Pair<Ride, String>>>()
    val userRides: LiveData<List<Pair<Ride, String>>> get() = _userRides

    private val _rideDeleted = MutableLiveData<Boolean>()
    val rideDeleted: LiveData<Boolean> get() = _rideDeleted

    private val _rideUpdated = MutableLiveData<Boolean>()
    val rideUpdated: LiveData<Boolean> get() = _rideUpdated

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

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

    fun loadUserRides() {
        if (userId == null) {
            _errorMessage.value = "User not logged in!"
            return
        }

        firestore.collection("rides")
            .whereEqualTo("userId", userId) // Filter rides by the logged-in user's ID
            .get()
            .addOnSuccessListener { querySnapshot ->
                val rides = querySnapshot.documents.mapNotNull { doc ->
                    val ride = doc.toObject(Ride::class.java)
                    ride?.let { Pair(it, doc.id) } // Pair Ride object with document ID
                }
                _userRides.value = rides
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Failed to load rides: ${exception.message}"
            }
    }

    fun deleteRide(documentId: String) {
        firestore.collection("rides")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                loadUserRides()
                _rideDeleted.value = true
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
                _rideDeleted.value = false
            }
    }

    fun updateRide(rideId: String, updatedRide: Ride) {
        val x = 2
        firestore.collection("rides")
            .document(rideId) // Reference the ride by its document ID
            .set(updatedRide, SetOptions.merge())
            .addOnSuccessListener {
                loadUserRides() // Refresh the list after updating
                _rideUpdated.value = true // Notify about successful update
            }
            .addOnFailureListener { exception ->
                _rideUpdated.value = false // Notify about update failure
                _errorMessage.value = exception.message // Provide detailed error message
            }
    }

}




