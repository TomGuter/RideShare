package com.example.shareride.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shareride.R
import com.example.shareride.model.FirebaseModel
import com.example.shareride.model.Ride
import com.example.shareride.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class RideDetailsFragment : Fragment() {

    private val firebaseModel = FirebaseModel()
    private var joinedUsers: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ride_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rideName = arguments?.getString("ride_name")
        val driverName = arguments?.getString("driver_name")
        val routeFrom = arguments?.getString("route_from")
        val routeTo = arguments?.getString("route_to")
        val date = arguments?.getString("date")
        val departureTime = arguments?.getString("departure_time")
        var rating = arguments?.getFloat("rating", 0.0f)
        val ratingCount = arguments?.getInt("ratingCount", 0)
        val ratingSum = arguments?.getFloat("ratingSum", 0.0f)
        val vacantSeats = arguments?.getInt("vacantSeats", 0)
        val userId = arguments?.getString("userId")
        val rideId = arguments?.getString("id")


        val rideNameTextView = view.findViewById<TextView>(R.id.ride_name_textview)
        val driverNameTextView = view.findViewById<TextView>(R.id.driver_name_textview)
        val departureLocationTextView =
            view.findViewById<TextView>(R.id.departure_location_textview)
        val arrivalLocationTextView = view.findViewById<TextView>(R.id.arrival_location_textview)
        val rideDateTextView = view.findViewById<TextView>(R.id.ride_date_textview)
        val departureTimeTextView = view.findViewById<TextView>(R.id.departure_time_textview)
        val ratingTextView = view.findViewById<TextView>(R.id.rating_textview)
        val vacantSeatsTextView = view.findViewById<TextView>(R.id.vacant_seats_textview)
        val driverImageView = view.findViewById<ImageView>(R.id.driver_image)
        val ratingBar = view.findViewById<RatingBar>(R.id.driver_rating_bar)
        val rateDriverButton = view.findViewById<Button>(R.id.rate_driver_button)
        val joinRideButton = view.findViewById<Button>(R.id.join_ride_button)
        val removeRideButton = view.findViewById<Button>(R.id.remove_ride_button)



        rideNameTextView.text = rideName
        driverNameTextView.text = "Driver: $driverName"
        departureLocationTextView.text = "From: $routeFrom"
        arrivalLocationTextView.text = "To: $routeTo"
        rideDateTextView.text = "Date: $date"
        departureTimeTextView.text = "Departure Time: $departureTime"
        ratingTextView.text = "Rating: $rating"
        vacantSeatsTextView.text = "Vacant Seats: $vacantSeats"
        ratingBar.rating = rating ?: 0.0f


        fun updateButtons(isJoined: Boolean) {
            if (isJoined) {
                joinRideButton.visibility = View.GONE
                removeRideButton.visibility = View.VISIBLE
            } else {
                joinRideButton.visibility = View.VISIBLE
                removeRideButton.visibility = View.GONE
            }
        }


        if (rideId != null) {
            val rideRef = FirebaseFirestore.getInstance().collection("rides").document(rideId)
            rideRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val ride = Ride.fromJSON(document.data ?: emptyMap())
                    joinedUsers = ride.joinedUsers.toMutableList()
                    updateButtons(joinedUsers.contains(firebaseModel.getCurrentUserId()))
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error fetching ride details. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        if (userId != null) {
            getUserPictureUrl(userId) { pictureUrl ->
                if (pictureUrl.isNotEmpty() && pictureUrl.startsWith("https://")) {

                    Picasso.get()
                        .load(pictureUrl)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(driverImageView)
                } else {
                    Picasso.get()
                        .load(R.drawable.avatar)
                        .into(driverImageView)
                }
            }
        }




        rateDriverButton.setOnClickListener {
            val newRating = ratingBar.rating

            if (userId == firebaseModel.getCurrentUserId()) {
                rateDriverButton.isEnabled = false
                rateDriverButton.text = "You cannot rate your own ride"
                rateDriverButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            } else if (userId != null) {
                val rideRef = FirebaseFirestore.getInstance().collection("rides").document(rideId!!)


                rating = String.format("%.2f", (ratingSum!! + newRating) / (ratingCount!! + 1)).toFloat()
                rideRef.update("rating", rating, "ratingSum", ratingSum + newRating, "ratingCount", ratingCount + 1)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ratingTextView.text = "Rating: $rating"
                            ratingBar.rating = newRating
                        } else {

                        }
                    }
            }
        }




        joinRideButton.setOnClickListener {
            if (rideId != null) {
                val currentUserId = firebaseModel.getCurrentUserId()
                if (currentUserId == userId) {
                    Toast.makeText(requireContext(), "You cannot join your own ride.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                FirebaseFirestore.getInstance().collection("rides").document(rideId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val ride = Ride.fromJSON(document.data ?: emptyMap())
                            val latestVacantSeats = ride.vacantSeats

                            if (!joinedUsers.contains(currentUserId) && latestVacantSeats > 0) {
                                if (currentUserId != null) {
                                    joinedUsers.add(currentUserId)
                                }
                                val updatedVacantSeats = latestVacantSeats - 1

                                FirebaseFirestore.getInstance().collection("rides").document(rideId)
                                    .update("joinedUsers", joinedUsers, "vacantSeats", updatedVacantSeats)
                                    .addOnSuccessListener {
                                        vacantSeatsTextView.text = "Vacant Seats: $updatedVacantSeats"
                                        updateButtons(true)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "Error joining the ride. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(requireContext(), "No vacant seats available or you're already in the ride.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error fetching ride details. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        removeRideButton.setOnClickListener {
            if (rideId != null) {
                val currentUserId = firebaseModel.getCurrentUserId()
                if (joinedUsers.contains(currentUserId)) {
                    // Fetch the latest ride data from Firestore to ensure we have the latest vacantSeats value
                    FirebaseFirestore.getInstance().collection("rides").document(rideId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val ride = Ride.fromJSON(document.data ?: emptyMap())
                                val latestVacantSeats = ride.vacantSeats // Get the latest vacant seats value from Firestore

                                joinedUsers.remove(currentUserId)
                                val updatedVacantSeats = latestVacantSeats + 1

                                FirebaseFirestore.getInstance().collection("rides").document(rideId)
                                    .update("joinedUsers", joinedUsers, "vacantSeats", updatedVacantSeats)
                                    .addOnSuccessListener {
                                        vacantSeatsTextView.text = "Vacant Seats: $updatedVacantSeats"
                                        updateButtons(false)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "Error removing from the ride. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error fetching ride details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "You are not part of this ride.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }



    private fun getUserPictureUrl(userId: String, callback: (String) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val json = document.data ?: emptyMap<String, Any>()
                    val user = User.fromJSON(json)
                    val pictureUrl = user.pictureUrl
                    callback(pictureUrl)

                } else {
                    callback("")
                }
            }
            .addOnFailureListener { exception ->
                callback("")
            }
    }



}


