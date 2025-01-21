package com.example.shareride.model


import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.storage.FirebaseStorage
import com.example.shareride.base.Constants
import com.example.shareride.base.EmptyCallback
import com.example.shareride.base.RidesCallback
import java.io.ByteArrayOutputStream

class FirebaseModel {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    init {
        val setting = com.google.firebase.firestore.firestoreSettings {
            setLocalCacheSettings(com.google.firebase.firestore.memoryCacheSettings { })
        }

        database.firestoreSettings = setting
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
            .set(ride.json)  // Assumes that the `ride.json` properly represents the updated data
            .addOnCompleteListener {
                callback()
            }
    }


    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}