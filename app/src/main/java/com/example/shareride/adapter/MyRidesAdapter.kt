




package com.example.shareride.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.model.Model
import com.example.shareride.model.Ride

class MyRidesAdapter(
    private var rideList: MutableList<com.example.shareride.model.Ride>,
    private val onDeleteClick: (com.example.shareride.model.Ride) -> Unit,
    private val onUpdateClick: (com.example.shareride.model.Ride) -> Unit
) : RecyclerView.Adapter<MyRidesAdapter.RideViewHolder>() {

    inner class RideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rideName: TextView = view.findViewById(R.id.ride_name)
        val driverName: TextView = view.findViewById(R.id.driver_name)
        val rideFrom: TextView = view.findViewById(R.id.ride_from)
        val rideTo: TextView = view.findViewById(R.id.ride_to)
        val departure: TextView = view.findViewById(R.id.departure)
        val rideDate: TextView = view.findViewById(R.id.ride_date)
        val phoneNumber: TextView = view.findViewById(R.id.phone_number)
        val vacantSeats: TextView = view.findViewById(R.id.vacant_seats)
        val deleteButton: Button = view.findViewById(R.id.btn_delete)
        val updateButton: Button = view.findViewById(R.id.btn_update)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val ride = rideList[position]
                    onDeleteClick(ride)
                }
            }

            updateButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val ride = rideList[position]
                    onUpdateClick(ride)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_created_rides, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rideList[position]


        holder.rideName.text = "Ride name: ${ride.name}"
        holder.driverName.text = "Driver: ${ride.driverName}"
        holder.rideFrom.text = "Ride from: ${ride.routeFrom}"
        holder.rideTo.text = "Ride to: ${ride.routeTo}"
        holder.departure.text = "Departure: ${ride.departureTime}"
        holder.rideDate.text = "Date: ${ride.date}"
        holder.vacantSeats.text = "Vacant seats: ${ride.vacantSeats}"

        ride.userId.let { userId ->
            Model.shared.getUser(userId) { user ->
                holder.phoneNumber.text = "Phone: ${user.phone}"
            }
        }    }

    override fun getItemCount(): Int = rideList.size

    fun updateRides(newRideList: List<com.example.shareride.model.Ride>) {
        rideList.clear()
        rideList.addAll(newRideList)
        notifyDataSetChanged()
    }
}




