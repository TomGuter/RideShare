package com.example.shareride.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.model.FirebaseModel
import com.example.shareride.model.Ride
import com.example.shareride.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class RideAdapter(private val rideList: MutableList<Ride>, private val onRideClick: (Ride) -> Unit) : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    inner class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rideName: TextView = itemView.findViewById(R.id.ride_name)
        private val driverName: TextView = itemView.findViewById(R.id.driver_name)
        private val rating: TextView = itemView.findViewById(R.id.rating)
        private val driverImage: ImageView = itemView.findViewById(R.id.driver_image)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)


        fun bind(ride: Ride) {
            rideName.text = ride.name
            driverName.text = ride.driverName
            rating.text = "Rating: ${ride.rating}"

            progressBar.visibility = View.VISIBLE


            getUserPictureUrl(ride.userId) { pictureUrl ->
                if (pictureUrl.isNotEmpty() && pictureUrl.startsWith("https://")) {
                    Picasso.get()
                        .load(pictureUrl)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(driverImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                progressBar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                progressBar.visibility = View.GONE
                            }
                        })
                } else {
                    Picasso.get()
                        .load(R.drawable.avatar)  // Default image when no URL
                        .into(driverImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                // Hide the ProgressBar once the image is loaded successfully
                                progressBar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                // Hide the ProgressBar if there's an error loading the image
                                progressBar.visibility = View.GONE
                            }
                        })
                }
            }


            itemView.setOnClickListener {
                onRideClick(ride)
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
