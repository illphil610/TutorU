package com.newwesterndev.tutoru.activities.Tutee

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.MessageActivity
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mFirebaseManager: FirebaseManager
    private var locationUpdateState = false
    private var availableTutorList = ArrayList<Model.Tutor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_maps)

        //intent = intent
        val lat = intent.getStringExtra("lat")
        val lon = intent.getStringExtra("lon")
        val course = intent.getStringExtra("course")

        // Get FirebaaseManager singleton
        mFirebaseManager = FirebaseManager.instance

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
            }
        }
        createLocationRequest()

        // geofire stuff
        val mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val mAvailTutorDatabaseReference = mFirebaseDatabase.getReference(Contract.AVAILABLE_TUTORS)
        val geofireAvailableTutor = GeoFire(mAvailTutorDatabaseReference)
        val geoQuery = geofireAvailableTutor.queryAtLocation(GeoLocation(lat.toDouble(), lon.toDouble()), 1000.0)

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                Log.e("TAG", String.format("Provider %s is within your search range [%f,%f]", key, location.latitude, location.longitude))
                mFirebaseManager.getTutor(key) { tutor ->
                    availableTutorList.add(tutor)
                    if (tutor.courseList.contains(course)) {
                        val tutorMapPin = map.addMarker(MarkerOptions().
                                position(LatLng(location.latitude, location.longitude))
                                .title(tutor.name))
                        tutorMapPin.snippet = "Avg Rating " + tutor.ratingAvg
                        tutorMapPin.isDraggable = true
                        tutorMapPin.tag = key
                    }
                }
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker?) {
                val intent = Intent(applicationContext, MessageActivity::class.java)
                intent.putExtra("userKey", marker?.tag.toString())
                startActivity(intent)
            }
            override fun onMarkerDrag(p0: Marker?) {}
            override fun onMarkerDragEnd(p0: Marker?) {}
        })
        setupMap()
    }



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
                //placeMarkerOnMap(currentLatLng)
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
        task.addOnFailureListener {e ->
            if (e is ResolvableApiException)
                try {
                    e.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx:IntentSender.SendIntentException) {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                locationUpdates()
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
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

    private fun hideStatusBar(){
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
            getWindow().getDecorView().setSystemUiVisibility(3328);
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
}
