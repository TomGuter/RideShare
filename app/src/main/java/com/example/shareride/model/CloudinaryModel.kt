package com.example.shareride.model

import android.content.Context
import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.cloudinary.android.policy.UploadPolicy
import com.example.shareride.base.MyApplication
import java.io.File
import java.io.FileOutputStream
import java.lang.Error
import kotlin.io.path.CopyActionContext

class CloudinaryModel {

    init {
        val config = mapOf(
            "cloud_name" to "dpvjpwmsf",
            "api_key" to "128983975163833",
            "api_secret" to "lJT0rGtl6EfciyrGqOrqTD3hcD4"
        )

        MyApplication.Globals.context?.let {
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.Builder()
                .maxConcurrentRequests(3)
                .networkPolicy(UploadPolicy.NetworkType.UNMETERED)
                .build()
        }
    }

    fun uploadBitmap(bitmap: Bitmap, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val context = MyApplication.Globals.context ?: return
        val file = bitmapToFile(bitmap, context)

        MediaManager.get().upload(file.path)
            .option(
                "folder",
                "images"
            )
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val publicUrl = resultData["secure_url"] as? String ?: ""
                    onSuccess(publicUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

            })
            .dispatch()
    }

    fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file
    }
}