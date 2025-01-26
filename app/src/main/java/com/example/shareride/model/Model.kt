package com.example.shareride.model

import android.graphics.Bitmap
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.shareride.R
import com.example.shareride.base.EmptyCallback
import com.example.shareride.base.RidesCallback
import com.example.shareride.model.dau.AppLocalDb
import com.example.shareride.model.dau.AppLocalDbRepository
import com.google.android.gms.auth.api.signin.internal.Storage
import java.util.concurrent.Executors

typealias SuccessCallback = (Boolean) -> Unit

class Model private constructor() {

    private val firebaseModel = FirebaseModel()
    private val cloudinaryModel = CloudinaryModel()

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val shared = Model()
    }


    fun getCurrentUserId(): String? {
        return firebaseModel.getCurrentUserId()
    }


    fun getAllRides(callback: RidesCallback) {
        firebaseModel.getAllRides(callback)

    }

//    fun getAllRides2(callback: RidesCallback) {
//        val lastUpdated: Long = Ride.lastUpdated
//
//        firebaseModel.getAllRides2(lastUpdated) { list ->
//            executor.execute {
//                var currentTime = lastUpdated
//                for (ride in list) {
//                    database.rideDao().insertRides(ride)
//                    ride.lastUpdated?.let {
//                        if (currentTime < it) {
//                            currentTime = it
//                        }
//                    }
//                }
//
//                Ride.lastUpdated = currentTime
//                val savedRides = database.rideDao().getAllRides()
//                mainHandler.post {
//                    callback(savedRides)
//                }
//            }
//
//        }
//
//    }

    fun getRidesByUserId(userId: String, callback: RidesCallback) {
        firebaseModel.getRidesByUserId(userId, callback)
    }

    fun addRide(ride: Ride, callback: SuccessCallback) {
        firebaseModel.addRide(ride, callback)
    }

    fun deleteRide(rideId: String, callback: EmptyCallback) {
        firebaseModel.deleteRide(rideId, callback)
    }

    fun updateRide(ride: Ride, callback: EmptyCallback) {
        firebaseModel.updateRide(ride, callback)
    }


    fun registerUser(firstName: String, lastName: String, email: String, phone: String, password: String, imageBitmap: Bitmap?, callback: (Boolean, String) -> Unit) {
        firebaseModel.registerUser(firstName, lastName, email, phone, password) { success, message ->
            if (success) {
                imageBitmap?.let {
                    uploadImageToCloudinary(it, email, { url ->
                        updateUser(firstName, lastName, email, phone, url) { updateSuccess, updateMessage ->
                            callback(updateSuccess, updateMessage)
                        }
                    }, { error ->
                        callback(false, "Image upload failed: $error")
                    })
                } ?: run {
                    val defaultAvatarUrl = "android.resource://com.example.shareride/${R.drawable.avatar}"
                    firebaseModel.updateUser(firstName, lastName, email, phone, defaultAvatarUrl) { updateSuccess, updateMessage ->
                        callback(updateSuccess, updateMessage)
                    }
                }
            } else {
                callback(false, message)
            }
        }
    }


    private fun updateUser(firstName: String, lastName: String, email: String, phone: String, pictureUrl: String, callback: (Boolean, String) -> Unit) {
        firebaseModel.updateUser(firstName, lastName, email, phone, pictureUrl, callback)
    }



    private fun uploadImageToCloudinary(image: Bitmap, name: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        cloudinaryModel.uploadBitmap(
            bitmap = image,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun getUser(userId: String, callback: (User) -> Unit) {
        firebaseModel.getUser(userId, callback)
    }


}
