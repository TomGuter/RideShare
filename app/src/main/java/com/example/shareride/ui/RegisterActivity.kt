package com.example.shareride.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shareride.MainActivity
import com.example.shareride.R
import com.example.shareride.model.Model
import com.example.shareride.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var avatarImageView: ImageView
    private lateinit var addImageButton: ImageButton
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var imageBitmap: Bitmap? = null
    private val CAMERA_PERMISSION_REQUEST = 100

    // Launchers for camera and gallery
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        avatarImageView = findViewById(R.id.avatarImageView)
        addImageButton = findViewById(R.id.addImageButton)
        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        phoneEditText = findViewById(R.id.phone_number_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        registerButton = findViewById(R.id.register_button)
        loginTextView = findViewById(R.id.login_text_view)

        avatarImageView.setImageResource(R.drawable.avatar)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageBitmap = bitmap
                avatarImageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Failed to capture image.", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                avatarImageView.setImageURI(uri)
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
            }
        }

        addImageButton.setOnClickListener {
            showImageOptionsDialog()
        }

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                Model.shared.registerUser(firstName, lastName, email, phone, password, imageBitmap) { success, message ->
                    if (success) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showImageOptionsDialog() {
        val options = mutableListOf("Choose from Gallery")

        val hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        if (hasCamera) {
            options.add("Take a Picture")
        } else {
            Toast.makeText(this, "No camera available.", Toast.LENGTH_SHORT).show()
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options.toTypedArray()) { _, which ->
            when (options[which]) {
                "Choose from Gallery" -> openGallery()
                "Take a Picture" -> checkCameraPermission()
            }
        }
        builder.show()
    }


    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }
    }

    private fun launchCamera() {
        cameraLauncher.launch(null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
        }
    }
}
