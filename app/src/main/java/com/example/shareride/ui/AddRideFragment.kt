package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shareride.R
import com.example.shareride.data.Ride
import com.example.shareride.viewmodel.RideViewModel
import com.google.android.material.textfield.TextInputEditText

class AddRideFragment : Fragment() {

    private val rideViewModel: RideViewModel by viewModels()

    private lateinit var rideNameInput: TextInputEditText
    private lateinit var driverNameInput: TextInputEditText
    private lateinit var routeFromInput: TextInputEditText
    private lateinit var routeToInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var departureTimeInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_ride, container, false)

        // Find views
        rideNameInput = view.findViewById(R.id.ride_name_input)
        driverNameInput = view.findViewById(R.id.driver_name_input)
        routeFromInput = view.findViewById(R.id.route_from_input)
        routeToInput = view.findViewById(R.id.route_to_input)
        dateInput = view.findViewById(R.id.date_input)
        departureTimeInput = view.findViewById(R.id.departure_time_input)

        // Observe ViewModel
        observeViewModel()

        view.findViewById<View>(R.id.add_ride_button).setOnClickListener {
            val ride = createRideFromInput()
            if (ride != null) {
                rideViewModel.addRideToDatabase(ride)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun observeViewModel() {
        rideViewModel.rideAdded.observe(viewLifecycleOwner) { isAdded ->
            if (isAdded) {
                Toast.makeText(requireContext(), "Ride added successfully", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed() // Navigate back after success
            }
        }

        rideViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error adding ride: $error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createRideFromInput(): Ride? {
        val rideName = rideNameInput.text.toString().trim()
        val driverName = driverNameInput.text.toString().trim()
        val routeFrom = routeFromInput.text.toString().trim()
        val routeTo = routeToInput.text.toString().trim()
        val date = dateInput.text.toString().trim()
        val departureTime = departureTimeInput.text.toString().trim()

        return if (rideName.isNotEmpty() && driverName.isNotEmpty() && routeFrom.isNotEmpty()
            && routeTo.isNotEmpty() && date.isNotEmpty() && departureTime.isNotEmpty()
        ) {
            Ride(
                name = rideName,
                driverName = driverName,
                routeFrom = routeFrom,
                routeTo = routeTo,
                date = date,
                departureTime = departureTime,
                rating = 0.0f
            )
        } else {
            null
        }
    }
}












//package com.example.shareride.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.example.shareride.R
//import com.example.shareride.data.Ride
//import com.google.android.material.textfield.TextInputEditText
//import com.google.firebase.firestore.FirebaseFirestore
//
//class AddRideFragment : Fragment() {
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var rideNameInput: TextInputEditText
//    private lateinit var driverNameInput: TextInputEditText
//    private lateinit var routeFromInput: TextInputEditText
//    private lateinit var routeToInput: TextInputEditText
//    private lateinit var dateInput: TextInputEditText
//    private lateinit var departureTimeInput: TextInputEditText
//    private lateinit var addRideButton: Button
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_add_ride, container, false)
//
//        // Initialize Firestore
//        firestore = FirebaseFirestore.getInstance()
//
//        // Find views
//        rideNameInput = view.findViewById(R.id.ride_name_input)
//        driverNameInput = view.findViewById(R.id.driver_name_input)
//        routeFromInput = view.findViewById(R.id.route_from_input)
//        routeToInput = view.findViewById(R.id.route_to_input)
//        dateInput = view.findViewById(R.id.date_input)
//        departureTimeInput = view.findViewById(R.id.departure_time_input)
//        addRideButton = view.findViewById(R.id.add_ride_button)
//
//        // Set button click listener
//        addRideButton.setOnClickListener {
//            val ride = createRideFromInput()
//            if (ride != null) {
//                addRideToDatabase(ride)
//            } else {
//                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        return view
//    }
//
//    private fun createRideFromInput(): Ride? {
//        val rideName = rideNameInput.text.toString().trim()
//        val driverName = driverNameInput.text.toString().trim()
//        val routeFrom = routeFromInput.text.toString().trim()
//        val routeTo = routeToInput.text.toString().trim()
//        val date = dateInput.text.toString().trim()
//        val departureTime = departureTimeInput.text.toString().trim()
//
//        return if (rideName.isNotEmpty() && driverName.isNotEmpty() && routeFrom.isNotEmpty()
//            && routeTo.isNotEmpty() && date.isNotEmpty() && departureTime.isNotEmpty()
//        ) {
//            Ride(
//                name = rideName,
//                driverName = driverName,
//                routeFrom = routeFrom,
//                routeTo = routeTo,
//                date = date,
//                departureTime = departureTime,
//                rating = 0.0f // Default rating
//            )
//        } else {
//            null
//        }
//    }
//
//    private fun addRideToDatabase(ride: Ride) {
//        firestore.collection("rides")
//            .add(ride)
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Ride added successfully", Toast.LENGTH_SHORT).show()
//                requireActivity().onBackPressed() // Navigate back after success
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(), "Error adding ride: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
