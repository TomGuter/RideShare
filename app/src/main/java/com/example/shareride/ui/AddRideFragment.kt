package com.example.shareride.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shareride.R
import com.example.shareride.model.Model
import com.example.shareride.model.Ride
import com.example.shareride.model.dau.AppLocalDb
import com.example.shareride.utils.OpenCageResponse
import com.example.shareride.utils.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.wms.BuildConfig
import retrofit2.Call
import java.util.*

class AddRideFragment : Fragment() {


    private lateinit var rideNameInput: TextInputEditText
    private lateinit var driverNameInput: TextInputEditText
    private lateinit var routeFromInput: TextInputEditText
    private lateinit var routeToInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var departureTimeInput: TextInputEditText
    private lateinit var vacantSeatsInput: TextInputEditText

    private lateinit var viewModel: RideListViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_ride, container, false)

        viewModel = ViewModelProvider(this, RideListViewModelFactory(AppLocalDb.rideDao))[RideListViewModel::class.java]        // Initialize UI components


        rideNameInput = view.findViewById(R.id.ride_name_input)
        driverNameInput = view.findViewById(R.id.driver_name_input)
        routeFromInput = view.findViewById(R.id.route_from_input)
        routeToInput = view.findViewById(R.id.route_to_input)
        dateInput = view.findViewById(R.id.date_input)
        departureTimeInput = view.findViewById(R.id.departure_time_input)
        vacantSeatsInput = view.findViewById(R.id.vacant_seats_input)

        dateInput.setOnClickListener {
            showDatePicker()
        }

        departureTimeInput.setOnClickListener {
            showTimePicker()
        }

        view.findViewById<View>(R.id.add_ride_button).setOnClickListener {
            val ride = createRideFromInput()
            if (ride != null) {
                getCoordinatesAndAddRide(ride)

            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    private fun createRideFromInput(): Ride? {
        val rideName = rideNameInput.text.toString().trim()
        val driverName = driverNameInput.text.toString().trim()
        val routeFrom = routeFromInput.text.toString().trim()
        val routeTo = routeToInput.text.toString().trim()
        val date = dateInput.text.toString().trim()
        val departureTime = departureTimeInput.text.toString().trim()
        val vacantSeatsStr = vacantSeatsInput.text.toString().trim()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        return if (rideName.isNotEmpty() && driverName.isNotEmpty() && routeFrom.isNotEmpty()
            && routeTo.isNotEmpty() && date.isNotEmpty() && departureTime.isNotEmpty()
            && vacantSeatsStr.isNotEmpty() && vacantSeatsStr.toIntOrNull() != null
        ) {
            val vacantSeats = vacantSeatsStr.toInt()

            Ride(
                id = generateUniqueId(),
                name = rideName,
                driverName = driverName,
                routeFrom = routeFrom,
                routeTo = routeTo,
                date = date,
                departureTime = departureTime,
                rating = 0.0f,
                userId = userId,
                latitude = 0.0,
                longitude = 0.0,
                vacantSeats = vacantSeats
            )
        } else {
            null
        }
    }

    private fun getCoordinatesAndAddRide(ride: Ride) {
        val apiKey = "7087fda4d1ab49d7b25608795548e6d5"
        val address = ride.routeFrom

        RetrofitClient.instance.getCoordinates(address, apiKey).enqueue(object : retrofit2.Callback<OpenCageResponse> {
            override fun onResponse(call: Call<OpenCageResponse>, response: retrofit2.Response<OpenCageResponse>) {
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    val location = response.body()!!.results[0].geometry
                    val updatedRide = ride.copy(latitude = location.latitude, longitude = location.longitude)

                    Model.shared.addRide(updatedRide) { success ->
                        activity?.runOnUiThread {
                            if (success) {
                                viewModel.addRide(updatedRide)
                                Toast.makeText(requireContext(), "Ride added successfully", Toast.LENGTH_SHORT).show()
                                requireActivity().onBackPressed()
                            } else {
                                Toast.makeText(requireContext(), "Failed to add ride", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch coordinates", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OpenCageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                dateInput.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                departureTimeInput.setText(selectedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }
}
