package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shareride.R

class RideDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ride_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting the data from the arguments
        val rideName = arguments?.getString("ride_name")
        val driverName = arguments?.getString("driver_name")
        val routeFrom = arguments?.getString("route_from")
        val routeTo = arguments?.getString("route_to")
        val date = arguments?.getString("date")
        val departureTime = arguments?.getString("departure_time")
        val rating = arguments?.getFloat("rating", 0.0f)

        // Finding the TextViews in the layout
        val rideNameTextView = view.findViewById<TextView>(R.id.ride_name_textview)
        val driverNameTextView = view.findViewById<TextView>(R.id.driver_name_textview)
        val departureLocationTextView = view.findViewById<TextView>(R.id.departure_location_textview)
        val arrivalLocationTextView = view.findViewById<TextView>(R.id.arrival_location_textview)
        val rideDateTextView = view.findViewById<TextView>(R.id.ride_date_textview)
        val departureTimeTextView = view.findViewById<TextView>(R.id.departure_time_textview)
        val ratingTextView = view.findViewById<TextView>(R.id.rating_textview)

        // Setting the data to the corresponding TextViews
        rideNameTextView.text = rideName
        driverNameTextView.text = "Driver: $driverName"
        departureLocationTextView.text = "From: $routeFrom"
        arrivalLocationTextView.text = "To: $routeTo"
        rideDateTextView.text = "Date: $date"
        departureTimeTextView.text = "Departure Time: $departureTime"
        ratingTextView.text = "Rating: $rating"
    }
}









//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.shareride.R
//import com.example.shareride.ui.RideDetailsFragmentArgs
//
//class RideDetailsFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_ride_details, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Get the arguments from SafeArgs
//        val args = RideDetailsFragmentArgs.fromBundle(requireArguments())
//
//        // Set the data to the UI elements
//        val rideNameTextView = view.findViewById<TextView>(R.id.ride_name_textview)
//        val driverNameTextView = view.findViewById<TextView>(R.id.driver_name_textview)
//        val departureLocationTextView = view.findViewById<TextView>(R.id.departure_location_textview)
//        val arrivalLocationTextView = view.findViewById<TextView>(R.id.arrival_location_textview)
//        val rideDateTextView = view.findViewById<TextView>(R.id.ride_date_textview)
//        val departureTimeTextView = view.findViewById<TextView>(R.id.departure_time_textview)
//        val ratingTextView = view.findViewById<TextView>(R.id.rating_textview)
//
//        // Set the received data
//        rideNameTextView.text = args.rideName
//        driverNameTextView.text = "Driver: ${args.driverName}"
//        departureLocationTextView.text = "From: ${args.routeFrom}"
//        arrivalLocationTextView.text = "To: ${args.routeTo}"
//        rideDateTextView.text = "Date: ${args.date}"
//        departureTimeTextView.text = "Departure Time: ${args.departureTime}"
//        ratingTextView.text = "Rating: ${args.rating}"
//    }
//}
//
//
//








//package com.example.shareride.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.shareride.R
//
//class RideDetailsFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_ride_details, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Getting the data from the arguments
//        val rideName = arguments?.getString("ride_name")
//        val driverName = arguments?.getString("driver_name")
//        val routeFrom = arguments?.getString("route_from")
//        val routeTo = arguments?.getString("route_to")
//        val date = arguments?.getString("date")
//        val departureTime = arguments?.getString("departure_time")
//        val rating = arguments?.getFloat("rating", 0.0f)
//
//        // Finding the TextViews in the layout
//        val rideNameTextView = view.findViewById<TextView>(R.id.ride_name_textview)
//        val driverNameTextView = view.findViewById<TextView>(R.id.driver_name_textview)
//        val departureLocationTextView = view.findViewById<TextView>(R.id.departure_location_textview)
//        val arrivalLocationTextView = view.findViewById<TextView>(R.id.arrival_location_textview)
//        val rideDateTextView = view.findViewById<TextView>(R.id.ride_date_textview)
//        val departureTimeTextView = view.findViewById<TextView>(R.id.departure_time_textview)
//        val ratingTextView = view.findViewById<TextView>(R.id.rating_textview)
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