package com.example.shareride.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.shareride.R
import com.example.shareride.model.Model
import com.squareup.picasso.Picasso

class PersonalArea : Fragment() {

    private lateinit var headlineTextView: TextView
    private lateinit var avatarImageView: ImageView
    private lateinit var addImageButton: ImageButton
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailTextView: TextView
    private lateinit var phoneNumberEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var uploadProgressBar: ProgressBar

    private var imageBitmap: Bitmap? = null
    private val GALLERY_REQUEST_CODE = 101
    private val CAMERA_PERMISSION_REQUEST = 102
    private val CAMERA_REQUEST_CODE = 103

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_area, container, false)

        headlineTextView = view.findViewById(R.id.headlineTextView)
        avatarImageView = view.findViewById(R.id.avatarImageView)
        addImageButton = view.findViewById(R.id.addImageButton)
        firstNameEditText = view.findViewById(R.id.first_name_edit_text)
        lastNameEditText = view.findViewById(R.id.last_name_edit_text)
        emailTextView = view.findViewById(R.id.email_text_view)
        phoneNumberEditText = view.findViewById(R.id.phone_number_edit_text)
        updateButton = view.findViewById(R.id.update_button)
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar)

        addImageButton.setOnClickListener { showImageOptionsDialog() }
        updateButton.setOnClickListener { handleUpdateButtonClick() }

        loadUserData()

        return view
    }

    private fun setProgressBarVisibility(isVisible: Boolean) {
        uploadProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun loadUserData() {
        Model.shared.getCurrentUserId()?.let { userId ->
            Model.shared.getUser(userId) { user ->
                user?.let {
                    firstNameEditText.setText(it.firstName)
                    lastNameEditText.setText(it.lastName)
                    emailTextView.text = it.email
                    phoneNumberEditText.setText(it.phone)
                    loadUserPicture(it.pictureUrl)
                }
            }
        }
    }



    private fun loadUserPicture(pictureUrl: String?) {
        setProgressBarVisibility(true)
        if (!pictureUrl.isNullOrEmpty() && pictureUrl.startsWith("https://")) {
            Picasso.get()
                .load(pictureUrl)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(avatarImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        setProgressBarVisibility(false)
                    }

                    override fun onError(e: Exception?) {
                        setProgressBarVisibility(false)
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Picasso.get()
                .load(R.drawable.avatar)
                .into(avatarImageView)
            setProgressBarVisibility(false)
        }
    }


    private fun showImageOptionsDialog() {
        val options = arrayOf("Choose from Gallery", "Take a Picture")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> checkCameraPermission()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        avatarImageView.setImageURI(imageUri)
                        imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        avatarImageView.setImageBitmap(bitmap)
                        imageBitmap = bitmap
                    }
                }
            }
        }
    }

    private fun handleUpdateButtonClick() {
        if (imageBitmap != null) {
            uploadImageToCloudinary { imageUrl ->
                updateUserProfile(imageUrl)
            }
        } else {
            updateUserProfile(null) // No image, just update text fields
        }
    }

    private fun uploadImageToCloudinary(onUploadComplete: (String) -> Unit) {
        imageBitmap?.let { bitmap ->
            setProgressBarVisibility(true)
            Model.shared.uploadImageToCloudinary(
                image = bitmap,
                name = "user_profile_picture",
                onSuccess = { url ->
                    uploadProgressBar.visibility = View.GONE
                    onUploadComplete(url)
                },
                onError = { error ->
                    setProgressBarVisibility(false)
                    Toast.makeText(context, "Failed to upload image: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateUserProfile(newPictureUrl: String?) {
        Model.shared.getCurrentUserId()?.let { userId ->
            Model.shared.getUser(userId) { user ->
                user?.let {
                    val updatedFirstName = firstNameEditText.text.toString().trim()
                    val updatedLastName = lastNameEditText.text.toString().trim()
                    val updatedPhone = phoneNumberEditText.text.toString().trim()

                    val updatedPictureUrl = newPictureUrl ?: it.pictureUrl

                    Model.shared.updateUser(
                        firstName = updatedFirstName,
                        lastName = updatedLastName,
                        email = it.email, // Email is not editable
                        phone = updatedPhone,
                        pictureUrl = updatedPictureUrl
                    ) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            loadUserPicture(updatedPictureUrl)
                        } else {
                            Toast.makeText(context, "Failed to update profile: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}