package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shareride.R
import com.google.firebase.auth.FirebaseAuth

class RideFragment : Fragment() {

    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ride, container, false)

        // Initialize the logout button
        logoutButton = view.findViewById(R.id.logout_button)

        // Set click listener for the logout button
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Log out the user
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()

            // Navigate back to the login screen
            activity?.let {
                it.finish() // Close the current activity
                it.startActivity(it.intent) // Restart the activity to reload the login screen
            }
        }

        return view
    }
}
