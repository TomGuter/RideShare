package com.example.shareride.viewmodel

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareride.model.Ride
import com.example.shareride.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RideViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _userRides = MutableLiveData<List<Pair<Ride, String>>>()
    val userRides: LiveData<List<Pair<Ride, String>>> get() = _userRides

    private val _rideAdded = MutableLiveData<Boolean>()
    val rideAdded: LiveData<Boolean> get() = _rideAdded

    private val _rideDeleted = MutableLiveData<Boolean>()
    val rideDeleted: LiveData<Boolean> get() = _rideDeleted

    private val _rideUpdated = MutableLiveData<Boolean>()
    val rideUpdated: LiveData<Boolean> get() = _rideUpdated

    private val _rideDetails = MutableLiveData<Ride>()
    val rideDetails: LiveData<Ride> get() = _rideDetails

    private val _rideCoordinates = MutableLiveData<Pair<Double, Double>>()
    val rideCoordinates: LiveData<Pair<Double, Double>> get() = _rideCoordinates

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile


    private val userId = FirebaseAuth.getInstance().currentUser?.uid



}


