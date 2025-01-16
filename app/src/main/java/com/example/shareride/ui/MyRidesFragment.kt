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
import com.google.firebase.firestore.FirebaseFirestore

class MyRidesFragment : Fragment() {

    private lateinit var rideViewModel: RideViewModel
    private lateinit var rideAdapter: MyRidesAdapter
    private lateinit var firestore: FirebaseFirestore
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

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyRides)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the Adapter
        rideAdapter = MyRidesAdapter(
            mutableListOf(),
            onDeleteClick = { ride -> deleteRide(ride) },
            onEditClick = { ride -> editRide(ride) }
        )
        recyclerView.adapter = rideAdapter

        // Fetch rides from Firestore
        fetchRidesFromDatabase()

        // Observe LiveData for user's rides (optional if using ViewModel LiveData)
        rideViewModel.userRides.observe(viewLifecycleOwner) { rides ->
            rideWithIdList.clear()
            rideWithIdList.addAll(rides)
            rideAdapter.updateRides(rides.map { it.first }) // Update adapter with Ride objects
        }

        rideViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun fetchRidesFromDatabase() {
        firestore.collection("rides")
            .get()
            .addOnSuccessListener { result ->
                rideWithIdList.clear() // Clear the list for the ride-document ID pairs

                // Iterate over the result to fetch rides and their document IDs
                for (document in result) {
                    val ride = document.toObject(Ride::class.java)
                    val documentId = document.id // Fetch the document ID

                    // Add the ride and document ID pair to the list
                    rideWithIdList.add(Pair(ride, documentId)) // Add the ride and document ID pair
                }

                // Update the RecyclerView adapter
                rideAdapter.updateRides(rideWithIdList.map { it.first })

            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rides: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteRide(ride: Ride) {
        // Find the document ID corresponding to the ride
        val documentId = rideWithIdList.find { it.first == ride }?.second

        if (documentId != null) {
            firestore.collection("rides").document(documentId).delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Ride deleted successfully!", Toast.LENGTH_SHORT).show()
                    fetchRidesFromDatabase() // Refresh the list after deletion
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error deleting ride: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Ride not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editRide(ride: Ride) {
        val documentId = rideWithIdList.find { it.first == ride }?.second

        if (documentId != null) {
            val bundle = Bundle().apply {
                putString("documentId", documentId)
                putString("ride_name", ride.name)
                // Add any other ride details to the bundle
            }
            findNavController().navigate(R.id.action_myRidesFragment_to_addRideFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Unable to edit this ride.", Toast.LENGTH_SHORT).show()
        }
    }
}
