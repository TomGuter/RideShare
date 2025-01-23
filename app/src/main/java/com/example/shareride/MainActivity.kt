package com.example.shareride

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.shareride.ui.LoginActivity
import com.example.shareride.model.Ride
import com.example.shareride.model.Model
import com.example.shareride.ui.OnFetchRidesListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker



class MainActivity : AppCompatActivity(), OnFetchRidesListener {

    private lateinit var navController: NavController
    private lateinit var map: MapView
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (isUserLoggedIn()) {
            navController.navigate(R.id.rideFragment)
        } else {
            openLoginActivity()
        }

        progressBar = findViewById(R.id.progressBar)
        map = findViewById(R.id.map)

        Configuration.getInstance().load(applicationContext, android.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)

        progressBar.visibility = View.VISIBLE


        val startPoint = GeoPoint(32.0853, 34.7818)
        map.controller.setZoom(12.0)
        map.controller.setCenter(startPoint)


        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Tel Aviv"
        map.overlays.add(marker)


        map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        })

        fetchRidesFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // osmdroid
    }

    private fun isUserLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show()
        openLoginActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                return true
            }
            R.id.action_rides -> {
                navController.navigate(R.id.rideFragment)
                return true
            }
            R.id.action_add_ride -> {
                navController.navigate(R.id.addRideFragment)
                return true
            }
            R.id.action_my_rides -> {
                navController.navigate(R.id.myRidesFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun updateMapWithRides(rides: List<Ride>) {
        map.overlays.clear()
        for (ride in rides) {
            val marker = Marker(map)
            val geoPoint = GeoPoint(ride.latitude, ride.longitude)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "ride from- ${ride.routeFrom} to-${ride.routeTo}"
            map.overlays.add(marker)
        }

        if (rides.isNotEmpty()) {
            map.controller.setCenter(GeoPoint(rides[0].latitude, rides[0].longitude))
            map.controller.setZoom(12.0)
        }
    }

    override fun fetchRidesFromDatabase() {
        val rides = mutableListOf<Ride>()
        Model.shared.getAllRides { ridesList ->
            ridesList.forEach { ride ->
                rides.add(ride)
            }
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("rides")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    with(document) {
                        rides.add(
                            Ride(
                                id = getString("id") ?: "",
                                name = getString("name") ?: "",
                                driverName = getString("driverName") ?: "",
                                routeFrom = getString("routeFrom") ?: "",
                                routeTo = getString("routeTo") ?: "",
                                date = getString("date") ?: "",
                                departureTime = getString("departureTime") ?: "",
                                ratingSum = getDouble("ratingSum")?.toFloat() ?: 0f,
                                ratingCount = getLong("ratingCount")?.toInt() ?: 0,
                                userId = getString("userId") ?: "",
                                latitude = getDouble("latitude") ?: 0.0,
                                longitude = getDouble("longitude") ?: 0.0,
                                vacantSeats = getLong("vacantSeats")?.toInt() ?: 0,
                                joinedUsers = (get("joinedUsers") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                            )
                        )
                    }
                }
                updateMapWithRides(rides)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateMapWithResults(results: List<GeoPoint>) {
        map.overlays.clear()
        for (location in results) {
            val marker = Marker(map)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "location"
            map.overlays.add(marker)
        }
        if (results.isNotEmpty()) {
            map.controller.setCenter(results[0])
            map.controller.setZoom(14.0) // zoom
        }
    }
}


