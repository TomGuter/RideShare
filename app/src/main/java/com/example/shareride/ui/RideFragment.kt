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
import com.example.shareride.data.Ride
import com.example.shareride.databinding.FragmentRideBinding
import com.google.firebase.firestore.FirebaseFirestore

class RideFragment : Fragment() {

    private lateinit var rideRecyclerView: RecyclerView
    private lateinit var rideAdapter: RideAdapter
    private val rideList = mutableListOf<Ride>()
    // Declare the list to hold pairs of Ride and its documentId
    private val rideWithIdList = mutableListOf<Pair<Ride, String>>()

    private val firestore = FirebaseFirestore.getInstance()

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

        // Fetch rides from the database
        fetchRidesFromDatabase()



        // Set up search functionality
        searchLocationEditText.addTextChangedListener {
            filterRides()
        }

        // Set up rating filter
        ratingFilterEditText.addTextChangedListener {
            filterRides()
        }

        return binding.root
    }


    private fun fetchRidesFromDatabase() {
        firestore.collection("rides")
            .get()
            .addOnSuccessListener { result ->
                rideList.clear() // Clear the existing list to avoid duplicates
                rideWithIdList.clear() // Clear the list for the ride-document ID pairs

                // Iterate over the result to fetch rides and their document IDs
                for (document in result) {
                    val ride = document.toObject(Ride::class.java)
                    val documentId = document.id // Fetch the document ID

                    // Add the ride and document ID pair to the list
                    rideList.add(ride) // Add ride to the ride list
                    rideWithIdList.add(Pair(ride, documentId)) // Add the ride and document ID pair
                }

                // Now you can use rideWithIdList to access both ride data and document ID
                rideWithIdList.forEach { rideWithId ->
                    val ride = rideWithId.first
                    val documentId = rideWithId.second
                    // Perform any operations with `ride` and `documentId`
                }

                rideAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rides: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



//     Fetch rides from Firestore
//    private fun fetchRidesFromDatabase() {
//        firestore.collection("rides")
//            .get()
//            .addOnSuccessListener { result ->
//                rideList.clear() // Clear the existing list to avoid duplicates
//                for (document in result) {
//                    val ride = document.toObject(Ride::class.java)
//                    rideList.add(ride)
//                }
//
//                rideAdapter.notifyDataSetChanged() // Notify adapter that data has changed
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(), "Error fetching rides: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }



    private fun filterRides() {
        val searchQuery = searchLocationEditText.text.toString().lowercase()

        // Ensure the rating input is parsed correctly
        val selectedRating = ratingFilterEditText.text.toString().toFloatOrNull() ?: 0f

        // Apply the filters (search and rating)
        val filteredRides = rideList.filter { ride ->
            val matchesLocation = (ride.routeFrom?.lowercase() ?: "").contains(searchQuery) ||
                    (ride.routeTo?.lowercase() ?: "").contains(searchQuery)
            val matchesRating = ride.rating >= selectedRating

            matchesLocation && matchesRating
        }


        if (searchQuery.isEmpty() && selectedRating == 0f) {
            rideAdapter.updateRides(rideList) // Show all rides
        } else {
            // Update the adapter with the filtered list
            rideAdapter.updateRides(filteredRides)
        }
    }

    private fun showRideDetails(ride: Ride) {
        // Use SafeArgs to pass data
        val action = RideFragmentDirections
            .actionRideFragmentToRideDetailsFragment(
                ride.name, ride.driverName, ride.routeFrom, ride.routeTo, ride.date, ride.departureTime, ride.rating
            )

        findNavController().navigate(action)
    }
}

