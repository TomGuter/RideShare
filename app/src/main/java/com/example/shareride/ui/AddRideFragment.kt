package com.example.shareride.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.google.firebase.auth.FirebaseAuth
import java.util.*

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

        rideNameInput = view.findViewById(R.id.ride_name_input)
        driverNameInput = view.findViewById(R.id.driver_name_input)
        routeFromInput = view.findViewById(R.id.route_from_input)
        routeToInput = view.findViewById(R.id.route_to_input)
        dateInput = view.findViewById(R.id.date_input)
        departureTimeInput = view.findViewById(R.id.departure_time_input)

        dateInput.setOnClickListener {
            showDatePicker()
        }

        departureTimeInput.setOnClickListener {
            showTimePicker()
        }

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
                requireActivity().onBackPressed()
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

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
                rating = 0.0f,
                userId = userId
            )
        } else {
            null
        }
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