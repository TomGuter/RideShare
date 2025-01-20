package com.example.shareride.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.data.Ride

class RideAdapter(private val rideList: MutableList<Ride>, private val onRideClick: (Ride) -> Unit) : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {


    inner class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rideName: TextView = itemView.findViewById(R.id.ride_name)
        private val driverName: TextView = itemView.findViewById(R.id.driver_name)
        private val rating: TextView = itemView.findViewById(R.id.rating)


        fun bind(ride: Ride) {
            rideName.text = ride.name
            driverName.text = ride.driverName
            rating.text = ride.rating.toString()

            itemView.setOnClickListener {
                onRideClick(ride)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ride, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rideList[position]
        holder.bind(ride)
    }

    override fun getItemCount(): Int {
        return rideList.size
    }

    fun updateRides(newRides: List<Ride>) {
        if (rideList != newRides) {
            rideList.clear()
            rideList.addAll(newRides)
            notifyDataSetChanged()
        }
    }

}
