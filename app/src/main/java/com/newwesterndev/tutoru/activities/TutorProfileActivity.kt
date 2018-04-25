package com.newwesterndev.tutoru.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
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
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model
import kotlinx.android.synthetic.main.activity_tutor_profile.*

class TutorProfileActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private val dbManager = DbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_profile)

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
        val subjectsFromDB = dbManager.getSubjects()
        val subjectNames = ArrayList<String>()

        for (subject in subjectsFromDB) {
            subjectNames.add(subject.name)
        }

        AlertDialog.Builder(this)
                .setTitle("Select Subjects")
                .setIcon(R.mipmap.ic_books)
                .setMultiChoiceItems(subjectNames.toTypedArray(), null) { _, indexSelected, isChecked ->
                    val checkedSubject: String = subjectNames[indexSelected]

                    if (isChecked) {
                        Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show()

                        val sharedPreferences: SharedPreferences by lazy {
                            this.getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
                        }
                        val editor = sharedPreferences.edit()
                        editor.putString(checkedSubject, checkedSubject)
                        editor.apply()

                        val fromPref = sharedPreferences.getString("subject", checkedSubject)
                        println(fromPref)

                    } else {
//                    tutorSubjects!!.removeAt(Integer.valueOf(indexSelected))
                        return@setMultiChoiceItems
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
//        Pass in items selected from subject selection dialog to getCourses
//        val coursesFromDB = dbManager.getCourses(checkedItems)
        val courseNames = ArrayList<String>()

//        for (course in coursesFromDB) {
//            courseNames.add(course.name)
//        }


        AlertDialog.Builder(this)
                .setTitle("Select Courses")
                .setIcon(R.mipmap.ic_books)
                .setMultiChoiceItems(courseNames.toTypedArray(), null) { dialogInterface, indexSelected, isChecked ->
                    if (isChecked) {

                    } else {

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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}
