package com.example.shareride

import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shareride.model.Ride
import com.example.shareride.ui.RideFragmentDirections
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(mapView: MapView, private val activity: MainActivity) : InfoWindow(R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as Marker
        val ride = marker.relatedObject as? Ride

        val rideFromTextView = mView.findViewById<TextView>(R.id.ride_from)
        val rideToTextView = mView.findViewById<TextView>(R.id.ride_to)
        val departureTextView = mView.findViewById<TextView>(R.id.departure)
        val viewRideButton = mView.findViewById<Button>(R.id.view_ride_button)
        val closeInfoWindowButton = mView.findViewById<Button>(R.id.close_button)

        if (ride != null) {
            rideFromTextView.text = "Ride from: ${ride.routeFrom}"
            rideToTextView.text = "Ride to: ${ride.routeTo}"
            departureTextView.text = "Departure: ${ride.departureTime}"
        }

        closeInfoWindowButton.setOnClickListener {
            close()
        }

        viewRideButton.setOnClickListener {
            val ride = marker.relatedObject as Ride

            val action = RideFragmentDirections.actionRideFragmentToRideDetailsFragment(
                ride.name,
                ride.driverName,
                ride.routeFrom,
                ride.routeTo,
                ride.date,
                ride.departureTime,
                ride.rating,
                ride.ratingCount,
                ride.ratingSum,
                ride.vacantSeats,
                ride.userId,
                ride.id
            )

            val navController = activity.navController
            if (navController.currentDestination?.id == R.id.rideFragment) {
                navController.navigate(action)
            } else {
                navController.popBackStack(R.id.rideFragment, false)
                navController.navigate(action)
            }
        }
    }

    override fun onClose() {
    }
}