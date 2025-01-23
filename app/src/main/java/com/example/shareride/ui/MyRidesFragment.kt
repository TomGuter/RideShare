package com.example.shareride.ui

import android.os.Bundle
import android.util.Log
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
import com.example.shareride.model.Model
import com.example.shareride.model.Ride
import com.example.shareride.model.User
import com.example.shareride.viewmodel.RideViewModel
import com.google.firebase.auth.FirebaseAuth
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


        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]


        firestore = FirebaseFirestore.getInstance()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyRides)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        rideAdapter = MyRidesAdapter(
            mutableListOf(),
            onDeleteClick = { ride -> deleteRide(ride) },
            onUpdateClick = { ride -> updateRide(ride) }
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Model.shared.getRidesByUserId(userId) { rides ->
                rideWithIdList.clear()
                for (ride in rides) {
                    val documentId = ride.id
                    rideWithIdList.add(Pair(ride, documentId))
                }
                rideAdapter.updateRides(rideWithIdList.map { it.first })
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun deleteRide(ride: Ride) {
        val documentId = rideWithIdList.find { it.first == ride }?.second
        if (documentId != null) {
            Model.shared.deleteRide(ride.id) {
                Toast.makeText(requireContext(), "Ride deleted successfully!", Toast.LENGTH_SHORT).show()
                fetchRidesFromDatabase()
            }
        } else {
            Toast.makeText(requireContext(), "Ride not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRide(ride: Ride) {
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
                putFloat("rating_sum", ride.ratingSum)
                putInt("rating_count", ride.ratingCount)
                putFloat("rating", ride.rating)
                putString("user_id", ride.userId)
                putDouble("latitude", ride.latitude)
                putDouble("longitude", ride.longitude)
                putInt("vacant_seats", ride.vacantSeats)
                putStringArrayList("joined_users", ArrayList(ride.joinedUsers))
                putString("ride_id", ride.id)

            }

            findNavController().navigate(R.id.action_myRidesFragment_to_updateRideFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Unable to edit this ride.", Toast.LENGTH_SHORT).show()
        }
    }


}
