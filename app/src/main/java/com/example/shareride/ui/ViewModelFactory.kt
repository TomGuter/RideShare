//package com.example.shareride.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.shareride.model.dau.RideDao
//
//class RideListViewModelFactory(private val rideDao: RideDao) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(RideListViewModel::class.java)) {
//            return RideListViewModel(rideDao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
