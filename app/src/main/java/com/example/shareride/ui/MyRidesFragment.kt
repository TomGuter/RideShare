package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.adapter.MyRidesAdapter
import com.example.shareride.data.Ride
import com.example.shareride.viewmodel.RideViewModel

class MyRidesFragment : Fragment() {

    private lateinit var rideViewModel: RideViewModel
    private lateinit var rideAdapter: MyRidesAdapter
    private val rideList = mutableListOf<Ride>()
    // Declare the list to hold pairs of Ride and its documentId
    private val rideWithIdList = mutableListOf<Pair<Ride, String>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_rides, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        rideViewModel = ViewModelProvider(this).get(RideViewModel::class.java)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyRides)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the Adapter
        rideAdapter = MyRidesAdapter(
            rideList,
            onDeleteClick = { ride -> deleteRide(ride) },
            onEditClick = { ride -> editRide(ride) }
        )
        recyclerView.adapter = rideAdapter

        // Observe LiveData for user's rides
        rideViewModel.userRides.observe(viewLifecycleOwner) { rides ->
            rideList.clear()
            rideList.addAll(rides)
            rideAdapter.notifyDataSetChanged()
        }

        // Load the logged-in user's rides
        rideViewModel.loadUserRides()
    }

    // Assuming you have a list of Pair<Ride, String> with ride and documentId
    private fun deleteRide(ride: Ride) {
        // Find the document ID corresponding to the ride
        val documentId = rideWithIdList.find { it.first == ride }?.second

        // If documentId is found, delete the ride
        if (documentId != null) {
            rideViewModel.deleteRide(documentId)
        } else {
            Toast.makeText(requireContext(), "Ride not found!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun editRide(ride: Ride) {
        // Handle editing the ride, e.g., navigate to an edit screen

        val documentId = rideWithIdList.find { it.first == ride }?.second


        val bundle = Bundle().apply {
            putString("documentId", documentId)
            putString("ride_name", ride.name)
            // Add any other ride details to the bundle
        }

        // Navigate to an edit fragment
        findNavController().navigate(R.id.action_myRidesFragment_to_addRideFragment, bundle)
    }
}
