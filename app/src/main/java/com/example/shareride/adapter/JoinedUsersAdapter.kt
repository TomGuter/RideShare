package com.example.shareride.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class JoinedUsersAdapter(private val userList: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<JoinedUsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.user_image)
        val userName: TextView = view.findViewById(R.id.user_name_textview)

        init {
            view.setOnClickListener {
                onItemClick(userList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_joined_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userId = userList[position]

        getUserPictureUrl(userId) { pictureUrl ->
            if (pictureUrl.isNotEmpty()) {
                Picasso.get().load(pictureUrl).into(holder.userImage)
            }
        }

        holder.userName.text = "User $userId"
    }

    override fun getItemCount() = userList.size

    private fun getUserPictureUrl(userId: String, callback: (String) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                val pictureUrl = document.getString("pictureUrl") ?: ""
                callback(pictureUrl)
            }
            .addOnFailureListener { callback("") }
    }
}
