package com.example.shareride.ui

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shareride.R
import com.example.shareride.base.Constants
import com.example.shareride.model.FirebaseModel
import com.example.shareride.model.Ride
import com.example.shareride.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class RideDetailsFragment : Fragment() {

    private val firebaseModel = FirebaseModel()


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
        val departureLocationTextView = view.findViewById<TextView>(R.id.departure_location_textview)
        val arrivalLocationTextView = view.findViewById<TextView>(R.id.arrival_location_textview)
        val rideDateTextView = view.findViewById<TextView>(R.id.ride_date_textview)
        val departureTimeTextView = view.findViewById<TextView>(R.id.departure_time_textview)
        val ratingTextView = view.findViewById<TextView>(R.id.rating_textview)
        val vacantSeatsTextView = view.findViewById<TextView>(R.id.vacant_seats_textview)
        val driverImageView = view.findViewById<ImageView>(R.id.driver_image)
        val ratingBar = view.findViewById<RatingBar>(R.id.driver_rating_bar)
        val rateDriverButton = view.findViewById<Button>(R.id.rate_driver_button)
        val driver_rating_bar = view.findViewById<RatingBar>(R.id.driver_rating_bar)

        rideNameTextView.text = rideName
        driverNameTextView.text = "Driver: $driverName"
        departureLocationTextView.text = "From: $routeFrom"
        arrivalLocationTextView.text = "To: $routeTo"
        rideDateTextView.text = "Date: $date"
        departureTimeTextView.text = "Departure Time: $departureTime"
        ratingTextView.text = "Rating: $rating"
        vacantSeatsTextView.text = "Vacant Seats: $vacantSeats"
        driver_rating_bar.rating = rating ?: 0.0f





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


