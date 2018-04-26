package com.newwesterndev.tutoru.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.LocationProxy
import kotlinx.android.synthetic.main.activity_help_request.*
import com.newwesterndev.tutoru.utilities.Utility

class HelpRequestActivity : AppCompatActivity(), LocationProxy.LocationDelegate {

    private lateinit var dbManager: DbManager
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var fbManager: FirebaseManager
    private lateinit var mUtil: Utility
    private lateinit var locationProxy: LocationProxy
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_request)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // location stuff and things...
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationProxy = LocationProxy(this, locationManager)
        locationProxy.mLocationDelegate = this
        locationProxy.requestUsersLocation()

        // firebase and databse stuff and things...
        fbManager = FirebaseManager.instance
        fbAuth = FirebaseAuth.getInstance()
        dbManager = DbManager(this)
        mUtil = Utility()
        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finishAffinity()
            } else {
                Log.e("USER_ID", fbAuth.currentUser?.uid)
            }
        }

        val subjectList = dbManager.getSubjectNamesAsString()
        val spinnerJawn = findViewById<Spinner>(R.id.subject_spinner)
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectList)
        spinnerJawn.adapter = spinnerAdapter

        submit_button.setOnClickListener {
            val mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
            var mDatabaseReference = mFirebaseDatabase.getReference(Contract.REQUESTING_HELP)
            val geoFireHelpRequest = GeoFire(mDatabaseReference)

            if (currentLocation != null) {

                // need to grab info from edit texts and spinners

                var list = ArrayList<Model.Course>()
                list.add(Model.Course("Geometry", "Math"))
                fbManager.sendHelpBroadcastRequest(
                        Model.HelpBroadCast(
                                // change this!!!!! just hacking it with the ! but check this please
                                Model.Tutee(fbAuth.currentUser!!.uid, "Phil McKracken", true),
                                list, true, "I have a math question because i suck at math"))

                geoFireHelpRequest.setLocation(fbAuth.currentUser?.uid, GeoLocation(currentLocation!!.latitude, currentLocation!!.longitude), { key, error ->
                    if (error != null) {
                        // fails omg no
                        Log.e("GEOFIRE", error.details)
                    } else {
                        // success
                        Log.e("GEOFIRE", "yahhhhhhhhh")
                        val intent = Intent(this, MapsActivity::class.java)
                        Log.e("Lat", currentLocation?.latitude.toString())
                        intent.putExtra("lat", currentLocation?.latitude.toString())
                        intent.putExtra("lon", currentLocation?.longitude.toString())
                        startActivity(intent)
                    }
                })
            } else {
                // need to make this like if location == null...then get location but aint nobody got time for that right now
                if (ActivityCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }

                fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.e("Lastknown", location.toString())
                                geoFireHelpRequest.setLocation(fbAuth.currentUser?.uid, GeoLocation(location.latitude, location.longitude), { key, error ->
                                    if (error != null) {
                                        // fails omg no
                                        Log.e("GEOFIRE", error.details)
                                    } else {
                                        // success
                                        Log.e("GEOFIRE", "yahhhhhhhhh")
                                        val intent = Intent(this, MapsActivity::class.java)
                                        Log.e("Lat", currentLocation?.latitude.toString())
                                        intent.putExtra("lat", location.latitude.toString())
                                        intent.putExtra("lon", location.longitude.toString())
                                        startActivity(intent)
                                    }
                                })
                            } else {
                                // we need to have this just grab the location or like tell them ot do stuff but this works for now ;)
                                Toast.makeText(this, "Is your location enabled?  try again please...", Toast.LENGTH_LONG).show()
                                Log.e("NO LOCAL", "no location yet fam, try again when you aint a bitch.")
                            }
                        }
            }
        }
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
        else -> { super.onOptionsItemSelected(item) }
    }

    override fun getUsersCurrentLocation(location: Location) {
        Log.e("locaion!!!", location.toString())
        currentLocation = location
        locationProxy.cancelUsersLocationRequest()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
