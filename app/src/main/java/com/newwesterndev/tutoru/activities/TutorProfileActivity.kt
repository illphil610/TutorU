package com.newwesterndev.tutoru.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import kotlinx.android.synthetic.main.activity_tutor_profile.*
import kotlinx.android.synthetic.main.custom_add_subject_dialog.*

class TutorProfileActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //Temporary static selection items
    private val subjects = arrayOf(" Programming ", " Math ", " Science ", " Writing ")
    private val selectedSubjects = arrayListOf<Int>()
    private val courses = arrayOf(" Java 1 ", " Calculus 1 ", " Biology ", " Technical Writing ")
    private val selectedCourses = arrayListOf<Int>()
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    // Firebase Stuff
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var dbManager: DbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_profile)

        fbAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        dbManager = DbManager(this)
        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finishAffinity()
            } else {
                Log.e("USER_ID", fbAuth.currentUser?.uid)
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.profile_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                map.clear()
                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        createLocationRequest()

        button_add_subject.setOnClickListener { openSubjectSelectDialog() }

        togglebutton_availibility.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Set tutor as active by placing them in the TutorsAvailable db table
                var mDatabaseReference = mFirebaseDatabase.getReference(Contract.AVAILABLE_TUTORS)
                val geoFireHelpRequest = GeoFire(mDatabaseReference)
                geoFireHelpRequest.setLocation(fbAuth.currentUser?.uid, GeoLocation(-1.3904519, -48.4673761), { key, error ->
                    if (error != null) {
                        // fails omg no
                        Log.e("GEOFIRE", error.details)
                    } else {
                        // success
                        Log.e("GEOFIRE", "yahhhhhhhhh")
                    }
                })

                val geoQuery = geoFireHelpRequest.queryAtLocation(GeoLocation(-1.3904519, -48.4673761), 100.0)
                geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
                    override fun onKeyEntered(key: String, location: GeoLocation) {
                        Log.e("TAG", String.format("Provider %s is within your search range [%f,%f]", key, location.latitude, location.longitude))
                    }

                    override fun onKeyExited(key: String) {
                        Log.i("TAG", String.format("Provider %s is no longer in the search area", key))
                    }

                    override fun onKeyMoved(key: String, location: GeoLocation) {
                        Log.i("TAG", String.format("Provider %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude))
                    }

                    override fun onGeoQueryReady() {
                        Log.i("TAG", "onGeoQueryReady")
                    }

                    override fun onGeoQueryError(error: DatabaseError) {
                        Log.e("TAG", "error: " + error)
                    }
                })
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setupMap()
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setupMap() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        map.addMarker(markerOptions)
        markerOptions.icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))
    }

    private fun locationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("RestrictedApi")
    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            locationUpdates()
        }
        task.addOnFailureListener { e ->
            if(e is ResolvableApiException)
                try{
                    e.startResolutionForResult(this@TutorProfileActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CHECK_SETTINGS) {
            if(resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                locationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if(!locationUpdateState) {
            locationUpdates()
        }
    }

    private fun openSubjectSelectDialog() {
        AlertDialog.Builder(this)
                .setTitle("Select Subjects")
                .setIcon(R.mipmap.ic_books)
                .setMultiChoiceItems(subjects, null) { _, indexSelected, isChecked ->
                    if (isChecked) {
                        selectedSubjects.add(indexSelected)
                    } else if (selectedSubjects.contains(indexSelected)) {
                        selectedSubjects.remove(Integer.valueOf(indexSelected))
                    }
                }
                .setPositiveButton("Now Select Courses") { _, _ ->
                    Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
                    openCourseSelectDialog()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
                }
                .create()
                .show()
    }

    private fun openCourseSelectDialog() {
        AlertDialog.Builder(this)
                .setTitle("Select Courses")
                .setIcon(R.mipmap.ic_books)
                .setMultiChoiceItems(courses, null) { _, indexSelected, isChecked ->
                    if (isChecked) {
                        selectedCourses.add(indexSelected)
                    } else if (selectedCourses.contains(indexSelected)) {
                        selectedCourses.remove(Integer.valueOf(indexSelected))
                    }
                }
                .setPositiveButton("Ok") { _, _ ->
                    Toast.makeText(this, "Courses Selected", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
                }
                .create()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_signout -> {
            fbAuth.signOut()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}
