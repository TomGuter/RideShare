package com.example.shareride

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.shareride.ui.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Check if the user is logged in and navigate accordingly
        if (isUserLoggedIn()) {
            // Navigate directly to the RideFragment
            navController.navigate(R.id.rideFragment)
        } else {
            // Direct the user to the LoginActivity
            openLoginActivity()
        }
    }

    // Check if the user is logged in
    private fun isUserLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
    }

    // Open LoginActivity if the user is not logged in
    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Logout the user and return to the LoginActivity
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show()
        openLoginActivity()
    }

    // Inflate the menu options for the toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Inflate the updated menu with Add Ride option
        return true
    }

    // Handle item selection from the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logoutUser() // Log out and go to LoginActivity
                return true
            }
            R.id.action_rides -> {
                // Navigate to the RideFragment
                navController.navigate(R.id.rideFragment)
                return true
            }
            R.id.action_add_ride -> {
                // Navigate to the AddRideFragment
                navController.navigate(R.id.addRideFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}











//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.NavController
//import androidx.navigation.Navigation
//import androidx.navigation.fragment.NavHostFragment
//import com.example.shareride.ui.LoginActivity
//import com.example.shareride.ui.RideFragment
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var logoutButton: Button
//    private lateinit var navController: NavController
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(this)
//
//        // Initialize NavController
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
//
//        // Check if the user is logged in
//        if (isUserLoggedIn()) {
//            // Directly show the RideFragment
//            navController.navigate(R.id.rideFragment)  // You need to have this action defined in your navigation graph
//        } else {
//            // Direct the user to the LoginActivity
//            openLoginActivity()
//        }
//
//        // Logout button logic
//        logoutButton = findViewById(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            logoutUser()
//        }
//    }
//
//    private fun openLoginActivity() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun isUserLoggedIn(): Boolean {
//        val user = FirebaseAuth.getInstance().currentUser
//        return user != null
//    }
//
//    private fun logoutUser() {
//        FirebaseAuth.getInstance().signOut()
//        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show()
//        openLoginActivity()
//    }
//}









//package com.example.shareride
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.startActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.shareride.ui.LoginActivity
//import com.example.shareride.ui.RideFragment
//import com.google.firebase.Firebase
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.QuerySnapshot
//import com.google.firebase.firestore.firestore
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var logoutButton: Button
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        FirebaseApp.initializeApp(this)
//
//        val db = Firebase.firestore
//
//
//
//        if (isUserLoggedIn()) {
//            openRideFragment()
//        } else {
//            openLoginActivity()
//        }
//
//
//        logoutButton = findViewById(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            logoutUser()
//        }
//    }
//
//
//
//
//    private fun openLoginActivity() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun openRideFragment() {
//        val rideFragment = RideFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, rideFragment)
//            .commit()
//
//
//    }
//
//
//    private fun isUserLoggedIn(): Boolean {
//        val user = FirebaseAuth.getInstance().currentUser
//        return user != null
//    }
//
//    private fun logoutUser() {
//        FirebaseAuth.getInstance().signOut()
//        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show()
//        openLoginActivity()
//    }
//
//}
//
//
//
