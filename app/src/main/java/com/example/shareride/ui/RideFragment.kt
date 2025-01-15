package com.example.shareride.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.adapter.RideAdapter
import com.example.shareride.data.Ride
import com.example.shareride.databinding.FragmentRideBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RideFragment : Fragment() {

    private lateinit var rideRecyclerView: RecyclerView
    private lateinit var rideAdapter: RideAdapter
    private val rideList = mutableListOf<Ride>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRideBinding.inflate(inflater, container, false)

        rideRecyclerView = binding.rideList
        rideAdapter = RideAdapter(rideList) { ride ->
            showRideDetails(ride)
        }

        rideRecyclerView.layoutManager = LinearLayoutManager(context)
        rideRecyclerView.adapter = rideAdapter


        fetchRidesFromDatabase()

        // Add a new ride (for example, this could be done when a button is clicked)
        val addRideButton = binding.root.findViewById<Button>(R.id.add_ride_button)
        addRideButton.setOnClickListener {
            val newRide = Ride(
                "New Ride",
                "Driver Z",
                "Location X",
                "Location Y",
                "2023-12-01",
                "06:00 PM",
                4.7f
            )
            addRideToDatabase(newRide)
        }

        return binding.root
    }

    // Fetch rides from Firestore
    private fun fetchRidesFromDatabase() {
        firestore.collection("rides")
            .get()
            .addOnSuccessListener { result ->
                rideList.clear() // Clear the existing list to avoid duplicates
                for (document in result) {
                    val ride = document.toObject(Ride::class.java)
                    rideList.add(ride)
                }
                rideAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rides: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addRideToDatabase(ride: Ride) {
        firestore.collection("rides")
            .add(ride)
            .addOnSuccessListener {
                rideList.add(ride)
                rideAdapter.notifyItemInserted(rideList.size - 1)
                Toast.makeText(requireContext(), "Ride added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error adding ride: ${exception.message}", Toast.LENGTH_SHORT).show()
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

//   private fun showRideDetails(ride: Ride) {
//    val bundle = Bundle().apply {
//        putString("ride_name", ride.name)
//        putString("driver_name", ride.driverName)
//        putString("route_from", ride.routeFrom)
//        putString("route_to", ride.routeTo)
//        putString("date", ride.date)
//        putString("departure_time", ride.departureTime)
//        putFloat("rating", ride.rating)
//    }
//
//    val rideDetailsFragment = RideDetailsFragment().apply {
//        arguments = bundle
//    }
//
//    parentFragmentManager.beginTransaction()
//        .replace(R.id.fragment_container, rideDetailsFragment)
//        .addToBackStack(null)
//        .commit()
//    }




}


