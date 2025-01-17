package com.example.shareride.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.shareride.R
import com.example.shareride.data.Ride
import com.example.shareride.viewmodel.RideViewModel

class UpdateRideFragment : Fragment() {

    private lateinit var documentId: String
    private lateinit var rideName: String
    private lateinit var driverName: String
    private lateinit var rideFrom: String
    private lateinit var rideTo: String
    private lateinit var rideDate: String
    private lateinit var rideTime: String

    private lateinit var rideViewModel: RideViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]


        arguments?.let {
            documentId = it.getString("documentId", "")
            rideName = it.getString("ride_name", "")
            driverName = it.getString("driver_name", "")
            rideFrom = it.getString("ride_from", "")
            rideTo = it.getString("ride_to", "")
            rideDate = it.getString("ride_date", "")
            rideTime = it.getString("ride_time", "")
        }

        // Populate the fields with the current ride data
        view.findViewById<EditText>(R.id.editTextRideName).setText(rideName)
        view.findViewById<EditText>(R.id.editTextDriverName).setText(driverName)
        view.findViewById<EditText>(R.id.editTextRideFrom).setText(rideFrom)
        view.findViewById<EditText>(R.id.editTextRideTo).setText(rideTo)
        view.findViewById<EditText>(R.id.editTextRideDate).setText(rideDate)
        view.findViewById<EditText>(R.id.editTextRideTime).setText(rideTime)

        // Handle save button click
        view.findViewById<Button>(R.id.saveRideButton).setOnClickListener {
            // Collect the updated values from the fields
            val updatedRideName = view.findViewById<EditText>(R.id.editTextRideName).text.toString()
            val updatedDriverName = view.findViewById<EditText>(R.id.editTextDriverName).text.toString()
            val updatedRideFrom = view.findViewById<EditText>(R.id.editTextRideFrom).text.toString()
            val updatedRideTo = view.findViewById<EditText>(R.id.editTextRideTo).text.toString()
            val updatedRideDate = view.findViewById<EditText>(R.id.editTextRideDate).text.toString()
            val updatedRideTime = view.findViewById<EditText>(R.id.editTextRideTime).text.toString()

            // Handle the save logic (e.g., update the ride in the database)
            saveRide(updatedRideName, updatedDriverName, updatedRideFrom, updatedRideTo, updatedRideDate, updatedRideTime)
        }
    }

    private fun saveRide(name: String, driver: String, from: String, to: String, date: String, time: String) {
        // Retrieve the documentId from the Bundle arguments
        val documentId = arguments?.getString("documentId")

        // Log the data for debugging
        Log.d("SaveRide", "Save ride called with: $name, $driver, $from, $to, $date, $time")

        if (documentId == null) {
            Log.e("SaveRide", "Error: Ride ID is missing!")
            Toast.makeText(requireContext(), "Error: Ride ID is missing!", Toast.LENGTH_SHORT).show()
            return
        }

        // Create an updated Ride object with the new values
        val updatedRide = Ride(
            name = name,
            driverName = driver,
            routeFrom = from,
            routeTo = to,
            date = date,
            departureTime = time
        )

        // Log before calling ViewModel
        Log.d("SaveRide", "Calling ViewModel to update ride with ID: $documentId")

        // Update the ride in Firestore through the ViewModel
        rideViewModel.updateRide(documentId, updatedRide)

        // Show success message
        Toast.makeText(requireContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show()

        // Navigate back to the previous fragment
        findNavController().popBackStack()
    }
}
