package com.example.shareride.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shareride.model.Ride


class RideViewModel : ViewModel() {


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _userRides = MutableLiveData<List<Pair<Ride, String>>>()
    val userRides: LiveData<List<Pair<Ride, String>>> get() = _userRides


}


