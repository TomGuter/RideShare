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


        var previousSearchQuery: String = ""
        var previousRatingQuery: String = ""

        searchLocationEditText.addTextChangedListener { editable ->
            val currentQuery = editable.toString()

            if (currentQuery.isEmpty()) {
                resetList()
            } else {
                applyFilters()
            }

            previousSearchQuery = currentQuery
        }



        ratingFilterEditText.addTextChangedListener { editable ->
            val currentQuery = editable.toString()

            if (currentQuery.isEmpty()) {
                resetList()
            } else {
                applyFilters()
            }

            previousRatingQuery = currentQuery
        }

        return binding.root
    }

    private fun fetchRidesFromDatabase() {
        Model.shared.getAllRides { rides ->
            rideList.clear()

            rides.forEach { ride ->
                rideList.add(ride)
            }

            rideAdapter.notifyDataSetChanged()
        }
    }

    private fun applyFilters() {
        val query = searchLocationEditText.text.toString().trim().lowercase()
        val minimumRating = ratingFilterEditText.text.toString().toFloatOrNull() ?: 0f

        val filteredRides = rideList.filter { ride ->
            val matchesLocation = ride.routeFrom?.contains(query, ignoreCase = true) == true ||
                    ride.routeTo?.contains(query, ignoreCase = true) == true
            val matchesRating = ride.rating >= minimumRating

            matchesLocation && matchesRating
        }

        rideAdapter.updateRides(
            if (query.isEmpty() && minimumRating == 0f) rideList else filteredRides
        )
    }


    private fun resetList() {
        fetchRidesFromDatabase()
    }

    private fun showRideDetails(ride: Ride) {
        val action = RideFragmentDirections
            .actionRideFragmentToRideDetailsFragment(
                ride.name, ride.driverName, ride.routeFrom, ride.routeTo, ride.date, ride.departureTime, ride.rating, ride.ratingCount,
                ride.ratingSum, ride.vacantSeats, ride.userId, ride.id,
            )

        findNavController().navigate(action)
    }



}
