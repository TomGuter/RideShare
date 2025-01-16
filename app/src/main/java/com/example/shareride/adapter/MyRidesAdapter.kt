package com.example.shareride.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.data.Ride

class MyRidesAdapter(
    private val rideList: List<Ride>,
    private val onDeleteClick: (Ride) -> Unit,
    private val onEditClick: (Ride) -> Unit
) : RecyclerView.Adapter<MyRidesAdapter.RideViewHolder>() {

    inner class RideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rideName: TextView = view.findViewById(R.id.ride_name)
        val driverName: TextView = view.findViewById(R.id.driver_name)
        val rating: TextView = view.findViewById(R.id.rating)
        val deleteButton: Button = view.findViewById(R.id.btn_delete)
        val updateButton: Button = view.findViewById(R.id.btn_update)

        init {
            // Handle delete button click
            deleteButton.setOnClickListener {
                val ride = rideList[adapterPosition]
                onDeleteClick(ride)
            }

            // Handle update button click
            updateButton.setOnClickListener {
                val ride = rideList[adapterPosition]
                onEditClick(ride)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_created_rides, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rideList[position]
        holder.rideName.text = ride.name
        holder.driverName.text = ride.driverName
        holder.rating.text = "Rating: ${ride.rating}"
    }

    override fun getItemCount(): Int {
        return rideList.size
    }
}
