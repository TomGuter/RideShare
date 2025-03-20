package com.example.shareride.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.shareride.model.Model
import com.example.shareride.model.Ride
import com.example.shareride.model.dau.AppLocalDb.rideDao
import com.example.shareride.utils.OpenCageResponse
import com.example.shareride.utils.RetrofitClient
import retrofit2.Call
//import com.example.shareride.viewmodel.RideViewModel
import java.util.*

class UpdateRideFragment : Fragment() {

    private lateinit var documentId: String
    private lateinit var rideName: String
    private lateinit var driverName: String
    private lateinit var rideFrom: String
    private lateinit var rideTo: String
    private lateinit var rideDate: String
    private lateinit var rideTime: String
    private var ratingSum: Float = 0f
    private var ratingCount: Int = 0
    private var rating: Float = 0f
    private lateinit var userId: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var vacantSeats: Int = 0
    private var joinedUsers: ArrayList<String> = arrayListOf()

    private lateinit var rideViewModel: RideListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rideViewModel = ViewModelProvider(this, RideListViewModelFactory(rideDao))[RideListViewModel::class.java]


        arguments?.let {
            documentId = it.getString("documentId", "")
            rideName = it.getString("ride_name", "")
            driverName = it.getString("driver_name", "")
            rideFrom = it.getString("ride_from", "")
            rideTo = it.getString("ride_to", "")
            rideDate = it.getString("ride_date", "")
            rideTime = it.getString("ride_time", "")
            ratingSum = it.getFloat("rating_sum", 0f)
            ratingCount = it.getInt("rating_count", 0)
            rating = it.getFloat("rating", 0f)
            userId = it.getString("user_id", "")
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
            vacantSeats = it.getInt("vacant_seats", 0)
            joinedUsers = it.getStringArrayList("joined_users") ?: arrayListOf()
        }

        view.findViewById<EditText>(R.id.editTextRideName).setText(rideName)
        view.findViewById<EditText>(R.id.editTextDriverName).setText(driverName)
        view.findViewById<EditText>(R.id.editTextRideFrom).setText(rideFrom)
        view.findViewById<EditText>(R.id.editTextRideTo).setText(rideTo)
        view.findViewById<EditText>(R.id.editTextRideDate).setText(rideDate)
        view.findViewById<EditText>(R.id.editTextRideTime).setText(rideTime)
        view.findViewById<EditText>(R.id.editTextVacantSeats).setText(vacantSeats.toString())

        val calendar = Calendar.getInstance()
        val dateEditText = view.findViewById<EditText>(R.id.editTextRideDate)
        dateEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    dateEditText.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val timeEditText = view.findViewById<EditText>(R.id.editTextRideTime)
        timeEditText.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }


        view.findViewById<Button>(R.id.saveRideButton).setOnClickListener {

            val updatedRideName = view.findViewById<EditText>(R.id.editTextRideName).text.toString()
            val updatedDriverName = view.findViewById<EditText>(R.id.editTextDriverName).text.toString()
            val updatedRideFrom = view.findViewById<EditText>(R.id.editTextRideFrom).text.toString()
            val updatedRideTo = view.findViewById<EditText>(R.id.editTextRideTo).text.toString()
            val updatedRideDate = view.findViewById<EditText>(R.id.editTextRideDate).text.toString()
            val updatedRideTime = view.findViewById<EditText>(R.id.editTextRideTime).text.toString()
            val updatedVacantSeats = view.findViewById<EditText>(R.id.editTextVacantSeats).text.toString().toInt()


            saveRide(updatedRideName, updatedDriverName, updatedRideFrom, updatedRideTo, updatedRideDate, updatedRideTime, updatedVacantSeats, rideFrom)
        }

    }

    private fun saveRide(name: String, driver: String, from: String, to: String, date: String, time: String, vacantSeats: Int, rideFrom: String) {
        val documentId = arguments?.getString("documentId")

        Log.d("SaveRide", "Save ride called with: $name, $driver, $from, $to, $date, $time")

        if (documentId == null) {
            Log.e("SaveRide", "Error: Ride ID is missing!")
            Toast.makeText(requireContext(), "Error: Ride ID is missing!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedRide = Ride(
            id = documentId,
            name = name,
            driverName = driver,
            routeFrom = from,
            routeTo = to,
            date = date,
            departureTime = time,
            ratingSum = ratingSum,
            ratingCount = ratingCount,
            rating = rating,
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            vacantSeats = vacantSeats,
            joinedUsers = joinedUsers
        )

        if (rideFrom != from) {
            fun getCoordinatesAndAddRide(ride: Ride) {
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
                                        rideViewModel.addRide(updatedRide)
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
            getCoordinatesAndAddRide(updatedRide)
        }



        Log.d("SaveRide", "Calling ViewModel to update ride with ID: $documentId")
        rideViewModel.updateRide(updatedRide)
        Toast.makeText(requireContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show()

        findNavController().popBackStack()
    }
}
