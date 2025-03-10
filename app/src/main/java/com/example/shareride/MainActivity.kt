package com.example.shareride

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.shareride.ui.LoginActivity
import com.example.shareride.model.Ride
import com.example.shareride.model.Model
import com.example.shareride.model.dau.AppLocalDb
import com.example.shareride.ui.RideListViewModel
import com.example.shareride.ui.RideListViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var map: MapView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: RideListViewModel


    private var currentInfoWindow: InfoWindow? = null
    private val handler = Handler(Looper.getMainLooper())
    private val loadingDelay = 1000L

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeLayout()
        initializeFirebase()
        setupNavigation()
        setupMap()
        setupLocationServices()



        viewModel = ViewModelProvider(this, RideListViewModelFactory(AppLocalDb.rideDao))[RideListViewModel::class.java]        // Initialize UI components

        viewModel.rides.observe(this, Observer { rides ->
            updateMapWithRides(rides)
        })
        viewModel.fetchRidesFromDatabase()

        refreshMap()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }
            R.id.action_rides -> {
                navController.navigate(R.id.rideFragment)
                true
            }
            R.id.action_add_ride -> {
                navController.navigate(R.id.addRideFragment)
                true
            }
            R.id.action_my_rides -> {
                navController.navigate(R.id.myRidesFragment)
                true
            }
            R.id.action_user -> {
                navController.navigate(R.id.personalAreaFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshMap() {
        val refreshButton = findViewById<FloatingActionButton>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            viewModel.fetchRidesFromDatabase()
        }
    }

    private fun initializeLayout() {
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        map = findViewById(R.id.map)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this, RideListViewModelFactory(AppLocalDb.rideDao))[RideListViewModel::class.java]        // Initialize UI components

    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (isUserLoggedIn()) {
            navController.navigate(R.id.rideFragment)
        } else {
            openLoginActivity()
        }
    }

    private fun setupMap() {
        Configuration.getInstance().load(applicationContext, android.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        map.setMultiTouchControls(true)
        val startPoint = GeoPoint(31.97019, 34.7766351)
        map.controller.setZoom(12.0)
        map.controller.setCenter(startPoint)
        map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                showLoadingIndicator()
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                showLoadingIndicator()
                return true
            }
        })
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show()
        openLoginActivity()
    }

    private fun addDefaultMarker(startPoint: GeoPoint) {
        val marker = Marker(map).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "I'm here!"
            icon = resources.getDrawable(R.drawable.red_marker_icon, null)
        }
        map.overlays.add(marker)
    }



    private fun updateMapWithRides(rides: List<Ride>) {
        val startPoint = GeoPoint(32.05588366083988, 34.85768581875001)
//        startPoint.latitude = location.latitude
//        startPoint.longitude = location.longitude
        map.overlays.clear()
        addDefaultMarker(startPoint)
        if (rides.isEmpty()) {
            Toast.makeText(this, "No rides found", Toast.LENGTH_SHORT).show()
            return
        }

        rides.forEach { ride ->
            val marker = Marker(map).apply {
                position = GeoPoint(ride.latitude, ride.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                infoWindow = CustomInfoWindow(map, this@MainActivity)
                relatedObject = ride
            }
            marker.setOnMarkerClickListener { m, _ ->
                handleMarkerClick(m)
                true
            }
            map.overlays.add(marker)
        }
        map.controller.setCenter(GeoPoint(rides[0].latitude, rides[0].longitude))
        map.controller.setZoom(12.0)
    }

    private fun handleMarkerClick(marker: Marker) {
        if (marker.isInfoWindowOpen) {
            marker.closeInfoWindow()
            currentInfoWindow = null
        } else {
            currentInfoWindow?.close()
            marker.showInfoWindow()
            currentInfoWindow = marker.infoWindow
        }
    }

    private fun showLoadingIndicator() {
        progressBar.visibility = View.VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ progressBar.visibility = View.GONE }, loadingDelay)
    }



    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = locationCallback()

        if (checkGPSPermission(locationCallback)) return

        getLastKnownGPSLocation()
        requestLocationUpdate(locationCallback)
    }


    private fun checkGPSPermission(locationCallback: LocationCallback): Boolean {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        getLastKnownGPSLocation()
                        requestLocationUpdate(locationCallback)
                    }

                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        getLastKnownGPSLocation()
                        requestLocationUpdate(locationCallback)
                    }

                    else -> {
                        // No location access granted.
                    }
                }
            }

            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return true
        }
        return false
    }

    private fun locationCallback() = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations) {

                this@MainActivity.location = location
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownGPSLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {
                    this.location = location

                }
            }
    }


    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate(locationCallback: LocationCallback) {

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            }.build(),
            locationCallback,
            Looper.getMainLooper()
        )
    }


}





