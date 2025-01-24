package com.example.shareride.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shareride.R
import com.example.shareride.model.User
import com.squareup.picasso.Picasso

class JoinedUsersAdapter(
    private val userList: MutableList<User>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<JoinedUsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.user_image)
        val userName: TextView = view.findViewById(R.id.user_name_textview)
        val phoneNumber: TextView = view.findViewById(R.id.user_phone_textview)

        init {
            view.setOnClickListener {
                val userId = userList[adapterPosition].id
                onItemClick(userId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_joined_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.image_progress)

        progressBar.visibility = View.VISIBLE

        if (user.pictureUrl.isNotEmpty()) {
            Picasso.get()
                .load(user.pictureUrl)
                .placeholder(R.drawable.avatar)
                .into(holder.userImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                    }
                })
        } else {
            holder.userImage.setImageResource(R.drawable.avatar)
            progressBar.visibility = View.GONE  // Hide spinner if no image URL
        }

        holder.userName.text = "Name: ${user.firstName}" ?: "Unknown User"
        holder.phoneNumber.text = "Phone: ${user.phone}" ?: "Unknown Phone Number"
    }

    override fun getItemCount() = userList.size

    fun updateUserList(newUserList: List<User>) {
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }
}
