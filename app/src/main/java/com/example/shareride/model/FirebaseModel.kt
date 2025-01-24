package com.example.shareride.model


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.example.shareride.MainActivity
import com.example.shareride.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.storage.FirebaseStorage
import com.example.shareride.base.Constants
import com.example.shareride.base.EmptyCallback
import com.example.shareride.base.MyApplication.Globals.context
import com.example.shareride.base.RidesCallback
import java.io.ByteArrayOutputStream

class FirebaseModel {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()



    init {
        val setting = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(com.google.firebase.firestore.memoryCacheSettings { })
        }

        database.firestoreSettings = setting
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    fun getAllRides(callback: RidesCallback) {
        database.collection(Constants.COLLECTIONS.RIDES).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val rides: MutableList<Ride> = mutableListOf()
                    for (document in task.result) {
                        rides.add(Ride.fromJSON(document.data))
                    }
                    callback(rides)
                } else {
                    callback(emptyList())
                }
            }
    }

    fun getRidesByUserId(userId: String, callback: RidesCallback) {
        database.collection(Constants.COLLECTIONS.RIDES).whereEqualTo("userId", userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val rides: MutableList<Ride> = mutableListOf()
                    for (document in task.result) {
                        rides.add(Ride.fromJSON(document.data))
                    }
                    callback(rides)
                } else {
                    callback(emptyList())
                }
            }
    }


    fun addRide(ride: Ride, callback: EmptyCallback) {
        database.collection(Constants.COLLECTIONS.RIDES).document(ride.id)
            .set(ride.json)
            .addOnCompleteListener {
                callback()
            }
    }

    fun deleteRide(rideId: String, callback: EmptyCallback) {
        database.collection(Constants.COLLECTIONS.RIDES).document(rideId)
            .delete()
            .addOnCompleteListener {
                callback()
            }
    }


    fun updateRide(ride: Ride, callback: EmptyCallback) {
        database.collection(Constants.COLLECTIONS.RIDES).document(ride.id)
            .set(ride.json)
            .addOnCompleteListener {
                callback()
            }
    }


    fun registerUser(firstName: String, lastName: String, email: String, phone: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""

                    val defaultAvatarUrl = "android.resource://com.example.shareride/${R.drawable.avatar}"

                    val newUser = User(
                        id = userId,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone,
                        pictureUrl = defaultAvatarUrl
                    )

                    user?.let {
                        database.collection("users").document(it.uid)
                            .set(newUser.json)
                            .addOnSuccessListener {
                                callback(true, "User registered successfully!")
                            }
                            .addOnFailureListener { e ->
                                callback(false, "Error saving user data: ${e.message}")
                            }
                    }
                } else {
                    callback(false, "Registration failed: ${task.exception?.message ?: "Unknown error"}")
                }
            }
    }



    fun updateUser(firstName: String, lastName: String, email: String, phone: String, pictureUrl: String, callback: (Boolean, String) -> Unit) {

        val userId = getCurrentUserId() ?: return callback(false, "User not logged in")

        val user = User(
            id = getCurrentUserId() ?: "",
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            pictureUrl = pictureUrl
        )

        database.collection("users").document(user.id)
            .set(user.json)
            .addOnSuccessListener {
                callback(true, "User data updated successfully!")
            }
            .addOnFailureListener { e ->
                callback(false, "Error updating user data: ${e.message}")
            }
    }


    fun getUser(userId: String, callback: (User) -> Unit) {
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.data?.let { User.fromJSON(it) }
                if (user != null) {
                    callback(user)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseModel", "Error getting user data", exception)
            }
    }
}