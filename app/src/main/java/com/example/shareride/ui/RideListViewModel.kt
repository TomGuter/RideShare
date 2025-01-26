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
                fetchRidesFromDatabase()
            } catch (e: Exception) {
                Log.e("RideListViewModel", "Error deleting ride", e)
            }
        }
    }
}