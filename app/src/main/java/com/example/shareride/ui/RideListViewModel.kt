package com.example.shareride.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareride.model.Model
import com.example.shareride.model.Ride
import com.example.shareride.model.dau.RideDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RideListViewModel(private val rideDao: RideDao) : ViewModel() {
    private val _rides = MutableLiveData<List<Ride>>()
    val rides: LiveData<List<Ride>> get() = _rides

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _userRides = MutableLiveData<List<Pair<Ride, String>>>()
    val userRides: LiveData<List<Pair<Ride, String>>> get() = _userRides


    fun fetchRidesFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Model.shared.getAllRides { fetchedRides ->
                    _rides.postValue(fetchedRides)
                }
            } catch (e: Exception) {
                Log.e("RideListViewModel", "Error fetching rides", e)
            }
        }
    }

    fun addRide(ride: Ride) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                rideDao.insertRides(*arrayOf(ride))
                fetchRidesFromDatabase()
            } catch (e: Exception) {
                Log.e("RideListViewModel", "Error inserting ride", e)
            }
        }
    }

    fun deleteRide(ride: Ride) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                rideDao.deleteRide(ride)

                Model.shared.deleteRide(ride.id) {
                    fetchRidesFromDatabase()
                }
            } catch (e: Exception) {
                Log.e("RideListViewModel", "Error deleting ride", e)
            }
        }
    }

    fun updateRide(ride: Ride) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                rideDao.updateRide(ride)
                Model.shared.updateRide(ride) {
                    fetchRidesFromDatabase()
                }
            } catch (e: Exception) {
                Log.e("RideListViewModel", "Error updating ride", e)
            }
        }
    }
}