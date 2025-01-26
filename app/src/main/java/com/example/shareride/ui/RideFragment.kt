package com.example.shareride.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.adapter.RideAdapter
import com.example.shareride.databinding.FragmentRideBinding
import com.example.shareride.model.Model
import com.example.shareride.model.Ride
import com.example.shareride.model.dau.AppLocalDb

class RideFragment : Fragment() {

    private lateinit var binding: FragmentRideBinding
    private lateinit var rideAdapter: RideAdapter
    private lateinit var viewModel: RideListViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var welcomeMessageTextView: TextView
    private lateinit var searchLocationEditText: EditText
    private lateinit var ratingFilterEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRideBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, RideListViewModelFactory(AppLocalDb.rideDao))[RideListViewModel::class.java]        // Initialize UI components

        initUI()


        observeViewModel()

        displayWelcomeMessage()

        setupFilters()

        return binding.root
    }

    private fun initUI() {
        progressBar = binding.progressBar
        welcomeMessageTextView = binding.welcomeMessage
        searchLocationEditText = binding.searchLocation
        ratingFilterEditText = binding.ratingFilter

        rideAdapter = RideAdapter(mutableListOf()) { ride ->
            showRideDetails(ride)
        }
        binding.rideList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rideAdapter
        }
    }

    private fun observeViewModel() {
        progressBar.visibility = View.VISIBLE

        viewModel.rides.observe(viewLifecycleOwner) { rides ->
            rideAdapter.updateRides(rides)
            progressBar.visibility = View.GONE
        }

        viewModel.fetchRidesFromDatabase()
    }

    private fun displayWelcomeMessage() {
        progressBar.visibility = View.VISIBLE
        welcomeMessageTextView.visibility = View.GONE

        val currUserId = Model.shared.getCurrentUserId()
        currUserId?.let { userId ->
            Model.shared.getUser(userId) { user ->
                val userName = user.firstName
                welcomeMessageTextView.text = "Hi $userName! Find Your Ride"
                progressBar.visibility = View.GONE
                welcomeMessageTextView.visibility = View.VISIBLE
            }
        } ?: run {
            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }

    private fun setupFilters() {
        searchLocationEditText.addTextChangedListener { applyFilters() }
        ratingFilterEditText.addTextChangedListener { applyFilters() }
    }

    private fun applyFilters() {
        val query = searchLocationEditText.text.toString().trim().lowercase()
        val minimumRating = ratingFilterEditText.text.toString().toFloatOrNull() ?: 0f

        viewModel.rides.value?.let { allRides ->
            val filteredRides = allRides.filter { ride ->
                val matchesLocation = ride.routeFrom.contains(query, ignoreCase = true) ||
                        ride.routeTo.contains(query, ignoreCase = true)
                val matchesRating = ride.rating >= minimumRating
                matchesLocation && matchesRating
            }

            rideAdapter.updateRides(filteredRides)
        }
    }

    private fun showRideDetails(ride: Ride) {
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
        findNavController().navigate(action)
    }
}
