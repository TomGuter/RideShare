package com.example.shareride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareride.data.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
}








//package com.example.shareride.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.shareride.data.Ride
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class RideViewModel : ViewModel() {
//
//    private val firestore = FirebaseFirestore.getInstance()
//
//    // LiveData for ride addition
//    private val _rideAdded = MutableLiveData<Boolean>()
//    val rideAdded: LiveData<Boolean> get() = _rideAdded
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> get() = _errorMessage
//
//    // LiveData for user-specific rides
//    private val _userRides = MutableLiveData<List<Ride>>()
//    val userRides: LiveData<List<Ride>> get() = _userRides
//
//    // LiveData for success or failure of updates
//    private val _rideUpdated = MutableLiveData<Boolean>()
//    val rideUpdated: LiveData<Boolean> get() = _rideUpdated
//
//    // LiveData for ride deletion success or failure
//    private val _rideDeleted = MutableLiveData<Boolean>()
//    val rideDeleted: LiveData<Boolean> get() = _rideDeleted
//
//
//    // Add a ride to the database
//    fun addRideToDatabase(ride: Ride) {
//        firestore.collection("rides")
//            .add(ride)
//            .addOnSuccessListener {
//                _rideAdded.value = true // Notify the Fragment that the ride was added successfully
//            }
//            .addOnFailureListener { exception ->
//                _errorMessage.value = exception.message // Notify the Fragment about the error
//            }
//    }
//
//    // Load rides created by the currently logged-in user
//    fun loadUserRides() {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            firestore.collection("rides")
//                .whereEqualTo("userId", userId) // Assuming each ride has a `userId` field
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    val rides = querySnapshot.documents.mapNotNull { it.toObject(Ride::class.java) }
//                    _userRides.value = rides
//                }
//                .addOnFailureListener { exception ->
//                    _errorMessage.value = exception.message // Handle error
//                }
//        } else {
//            _errorMessage.value = "User is not logged in"
//        }
//    }
//
//    // Delete a ride
//    fun deleteRide(documentId: String) {
//        firestore.collection("rides")
//            .document(documentId) // Use the documentId directly
//            .delete()
//            .addOnSuccessListener {
//                loadUserRides() // Refresh the list after deletion
//            }
//            .addOnFailureListener { exception ->
//                _errorMessage.value = exception.message // Notify about the error
//            }
//    }
//
//
//    // Update a ride in the database
//    fun updateRide(rideId: String, updatedRide: Ride) {
//        firestore.collection("rides")
//            .document(rideId) // Reference the ride by its document ID
//            .set(updatedRide) // Set the updated ride data
//            .addOnSuccessListener {
//                loadUserRides() // Refresh the list after updating
//                _rideUpdated.value = true // Notify about successful update
//            }
//            .addOnFailureListener { exception ->
//                _rideUpdated.value = false // Notify about update failure
//                _errorMessage.value = exception.message // Notify about the error
//            }
//    }
//}
//
//
//
//
//
