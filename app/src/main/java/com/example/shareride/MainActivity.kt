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
import org.osmdroid.views.overlay.infowindow.InfoWindow
import android.os.Handler
import android.os.Looper


class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var map: MapView
    private lateinit var progressBar: ProgressBar
    private var currentInfoWindow: InfoWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (isUserLoggedIn()) {
            navController.navigate(R.id.rideFragment)
        } else {
            openLoginActivity()
        }

        progressBar = findViewById(R.id.progressBar)
        map = findViewById(R.id.map)

        Configuration.getInstance().load(
            applicationContext,
            android.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        map.setMultiTouchControls(true)


        val startPoint = GeoPoint(32.0853, 34.7818)
        map.controller.setZoom(12.0)
        map.controller.setCenter(startPoint)

        addDefaultMarker(startPoint)

        map.addMapListener(object : MapListener {
            private val handler = Handler(Looper.getMainLooper())
            private val loadingDelay = 1000L

            override fun onScroll(event: ScrollEvent?): Boolean {
                progressBar.visibility = View.VISIBLE

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    progressBar.visibility = View.GONE
                }, loadingDelay)

                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                progressBar.visibility = View.VISIBLE

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    progressBar.visibility = View.GONE
                }, loadingDelay)

                return true
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
        map.onPause()
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
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

    private fun addDefaultMarker(startPoint: GeoPoint) {
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Tel Aviv"
        map.overlays.add(marker)
    }

    private fun updateMapWithRides(rides: List<Ride>) {
        map.overlays.clear()
        if (rides.isEmpty()) {
            Toast.makeText(this, "No rides found", Toast.LENGTH_SHORT).show()
            return
        }

        for (ride in rides) {
            val marker = Marker(map)
            val geoPoint = GeoPoint(ride.latitude, ride.longitude)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//            marker.title = "Ride from: ${ride.routeFrom} Ride to: ${ride.routeTo} Departure: ${ride.departureTime}"
            marker.infoWindow = CustomInfoWindow(map, this)
            marker.relatedObject = ride

            marker.setOnMarkerClickListener { m, _ ->
                if (m.isInfoWindowOpen) {
                    m.closeInfoWindow()
                    currentInfoWindow = null
                } else {
                    currentInfoWindow?.close()
                    m.showInfoWindow()
                    currentInfoWindow = m.infoWindow
                }
                true
            }

            map.overlays.add(marker)
        }

        map.controller.setCenter(GeoPoint(rides[0].latitude, rides[0].longitude))
        map.controller.setZoom(12.0)
    }

    private fun fetchRidesFromDatabase() {
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
            marker.title = "Location"
            map.overlays.add(marker)
        }
        if (results.isNotEmpty()) {
            map.controller.setCenter(results[0])
            map.controller.setZoom(14.0)
        }
    }
}
