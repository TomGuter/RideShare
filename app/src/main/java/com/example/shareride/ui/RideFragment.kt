package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.adapter.RideAdapter
import com.example.shareride.databinding.FragmentRideBinding
import com.example.shareride.model.Model
import com.example.shareride.base.RidesCallback
import com.example.shareride.base.EmptyCallback
import com.example.shareride.model.Ride

class RideFragment : Fragment() {

    private lateinit var rideRecyclerView: RecyclerView
    private lateinit var rideAdapter: RideAdapter
    private val rideList = mutableListOf<Ride>()
    private val rideWithIdList = mutableListOf<Pair<Ride, String>>()

    private lateinit var searchLocationEditText: EditText
    private lateinit var ratingFilterEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRideBinding.inflate(inflater, container, false)

        rideRecyclerView = binding.rideList
        searchLocationEditText = binding.searchLocation
        ratingFilterEditText = binding.ratingFilter

        rideAdapter = RideAdapter(rideList) { ride ->
            showRideDetails(ride)
        }

        rideRecyclerView.layoutManager = LinearLayoutManager(context)
        rideRecyclerView.adapter = rideAdapter

        fetchRidesFromDatabase()

        searchLocationEditText.addTextChangedListener {
            filterRides()
        }

        ratingFilterEditText.addTextChangedListener {
            filterRides()
        }

        return binding.root
    }

    private fun fetchRidesFromDatabase() {
        Model.shared.getAllRides { rides ->
            rideList.clear()
            rideWithIdList.clear()

            rides.forEach { ride ->
                rideList.add(ride)
                rideWithIdList.add(Pair(ride, ride.userId))  // For example, using userId as the ID
            }

            rideAdapter.notifyDataSetChanged() // Notify adapter that data has changed
        }
    }

    private fun filterRides() {
        val searchQuery = searchLocationEditText.text.toString().lowercase()

        val selectedRating = ratingFilterEditText.text.toString().toFloatOrNull() ?: 0f

        val filteredRides = rideList.filter { ride ->
            val matchesLocation = (ride.routeFrom?.lowercase() ?: "").contains(searchQuery) ||
                    (ride.routeTo?.lowercase() ?: "").contains(searchQuery)
            val matchesRating = ride.rating >= selectedRating

            matchesLocation && matchesRating
        }

        if (searchQuery.isEmpty() && selectedRating == 0f) {
            rideAdapter.updateRides(rideList) // Show all rides
        } else {
            rideAdapter.updateRides(filteredRides)
        }
    }

    private fun showRideDetails(ride: Ride) {
        val action = RideFragmentDirections
            .actionRideFragmentToRideDetailsFragment(
                ride.name, ride.driverName, ride.routeFrom, ride.routeTo, ride.date, ride.departureTime, ride.rating, ride.vacantSeats
            )

        findNavController().navigate(action)
    }
}
