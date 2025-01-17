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


        rideViewModel = ViewModelProvider(this).get(RideViewModel::class.java)


        firestore = FirebaseFirestore.getInstance()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyRides)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        rideAdapter = MyRidesAdapter(
            mutableListOf(),
            onDeleteClick = { ride -> deleteRide(ride) },
            onEditClick = { ride -> editRide(ride) }
        )
        recyclerView.adapter = rideAdapter

        fetchRidesFromDatabase()

        rideViewModel.userRides.observe(viewLifecycleOwner) { rides ->
            rideWithIdList.clear()
            rideWithIdList.addAll(rides)
            rideAdapter.updateRides(rides.map { it.first })
        }

        rideViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun fetchRidesFromDatabase() {
        firestore.collection("rides")
            .get()
            .addOnSuccessListener { result ->
                rideWithIdList.clear()

                for (document in result) {
                    val ride = document.toObject(Ride::class.java)
                    val documentId = document.id

                    rideWithIdList.add(Pair(ride, documentId))
                }

                rideAdapter.updateRides(rideWithIdList.map { it.first })

            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rides: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteRide(ride: Ride) {
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
                putString("driver_name", ride.driverName)
                putString("ride_from", ride.routeFrom)
                putString("ride_to", ride.routeTo)
                putString("ride_date", ride.date)
                putString("ride_time", ride.departureTime)

            }

            findNavController().navigate(R.id.action_myRidesFragment_to_updateRideFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Unable to edit this ride.", Toast.LENGTH_SHORT).show()
        }
    }







}
