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
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var map: MapView // משתנה עבור המפה

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // אתחול Firebase
        FirebaseApp.initializeApp(this)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // בדיקת משתמש מחובר
        if (isUserLoggedIn()) {
            navController.navigate(R.id.rideFragment)
        } else {
            openLoginActivity()
        }

        // אתחול הגדרות OpenStreetMap
        Configuration.getInstance().load(applicationContext, android.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        // אתחול MapView
        map = findViewById(R.id.map) // מזהה את המפה מ-XML
        map.setMultiTouchControls(true) // מאפשר זום עם שתי אצבעות

        // מרכז את המפה למיקום מסוים (לדוגמה: תל אביב)
        val startPoint = GeoPoint(32.0853, 34.7818) // קואורדינטות של תל אביב
        map.controller.setZoom(12.0) // רמת זום
        map.controller.setCenter(startPoint)

        // הוספת סימון למיקום
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "תל אביב"
        map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        map.onResume() // נדרש עבור osmdroid
    }

    override fun onPause() {
        super.onPause()
        map.onPause() // נדרש עבור osmdroid
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

    /**
     * מעדכנת את המפה עם תוצאות חיפוש
     * @param results רשימה של נקודות גיאוגרפיות להצגה במפה
     */
    fun updateMapWithResults(results: List<GeoPoint>) {
        map.overlays.clear() // מנקה את המפה
        for (location in results) {
            val marker = Marker(map)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "מיקום שנמצא"
            map.overlays.add(marker)
        }
        if (results.isNotEmpty()) {
            map.controller.setCenter(results[0]) // ממקד למיקום הראשון בתוצאות
            map.controller.setZoom(14.0) // רמת זום
        }
    }
}
