//package com.example.shareride.ui
//
//import android.os.Bundle
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.shareride.R
//
//class RideDetailsActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ride_details)
//
//
//        val rideName = intent.getStringExtra("ride_name")
//        val driverName = intent.getStringExtra("driver_name")
//        val routeFrom = intent.getStringExtra("route_from")
//        val routeTo = intent.getStringExtra("route_to")
//        val date = intent.getStringExtra("date")
//        val departureTime = intent.getStringExtra("departure_time")
//        val rating = intent.getFloatExtra("rating", 0.0f)
//
//
//        val rideNameTextView = findViewById<TextView>(R.id.ride_name_textview)
//        val driverNameTextView = findViewById<TextView>(R.id.driver_name_textview)
//        val departureLocationTextView = findViewById<TextView>(R.id.departure_location_textview)
//        val arrivalLocationTextView = findViewById<TextView>(R.id.arrival_location_textview)
//        val rideDateTextView = findViewById<TextView>(R.id.ride_date_textview)
//        val departureTimeTextView = findViewById<TextView>(R.id.departure_time_textview)
//        val ratingTextView = findViewById<TextView>(R.id.rating_textview)
//
//        // Setting the data to the corresponding TextViews
//        rideNameTextView.text = rideName
//        driverNameTextView.text = "Driver: $driverName"
//        departureLocationTextView.text = "From: $routeFrom"
//        arrivalLocationTextView.text = "To: $routeTo"
//        rideDateTextView.text = "Date: $date"
//        departureTimeTextView.text = "Departure Time: $departureTime"
//        ratingTextView.text = "Rating: $rating"
//    }
//}