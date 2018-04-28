package com.newwesterndev.tutoru.activities.Tutee

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
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
import android.view.Gravity
import android.widget.TextView
import com.newwesterndev.tutoru.activities.MessageActivity
import com.newwesterndev.tutoru.activities.SessionActivity


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

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION), 10)

        // location stuff and things...
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationProxy = LocationProxy(this, locationManager)
        locationProxy.mLocationDelegate = this

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationProxy.requestUsersLocation()
        }

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

        user_name_greeting.text = """Hello, ${fbAuth.currentUser?.displayName}"""
        val subjectList = dbManager.getSubjectNamesAsString()
        val subjectSpinnerList = dbManager.getSubjectListForSpinner(subjectList)
        val spinnerJawn = findViewById<Spinner>(R.id.subject_spinner)
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectSpinnerList)
        spinnerJawn.adapter = spinnerAdapter

        // Hide views until the user selects a subject
        course_spinner.visibility = (View.GONE)
        course_text_view.visibility = (View.GONE)
        question_text_view.visibility = (View.GONE)
        question_edit_text.visibility = (View.GONE)
        submit_button.visibility = (View.GONE)

        spinnerJawn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent?.getChildAt(0) as TextView).gravity = Gravity.CENTER
                if (position != 0) {
                    // Show the views to get courses that are hidden
                    course_text_view.visibility = (View.VISIBLE)
                    course_spinner.visibility = (View.VISIBLE)
                    question_text_view.visibility = (View.VISIBLE)
                    question_edit_text.visibility = (View.VISIBLE)
                    submit_button.visibility = (View.VISIBLE)

                    val subjectName = subjectList[position - 1]
                    val coursesFromSubject = dbManager.getCoursesAsString(subjectName)
                    val courseSpinnerList = dbManager.getCourseListForSpinner(coursesFromSubject)
                    val courseSpinnerJawn = findViewById<Spinner>(R.id.course_spinner)
                    val courseSpinnerAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, courseSpinnerList)
                    courseSpinnerJawn.adapter = courseSpinnerAdapter

                    courseSpinnerJawn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            (parent?.getChildAt(0) as TextView).gravity = Gravity.CENTER
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        submit_button.setOnClickListener {
            val mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
            var mDatabaseReference = mFirebaseDatabase.getReference(Contract.REQUESTING_HELP)
            val geoFireHelpRequest = GeoFire(mDatabaseReference)

            val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
            val userFcmId = preferences.getString(getString(R.string.FCM_ID), "no fcm")

            // need to grab property fields here and use them below

            if (currentLocation != null) {
                //var list = ArrayList<Model.Course>()
                //list.add(Model.Course("Geometry", "Math"))
                fbManager.sendHelpBroadcastRequest(
                        Model.HelpBroadCast(
                                Model.Tutee(fbAuth.currentUser!!.uid,
                                            userFcmId,
                                            fbAuth.currentUser?.displayName.toString(), "0.0", "0", true),
                                            course_spinner.selectedItem.toString(), true, question_edit_text.text.toString()))

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
                        intent.putExtra("course", course_spinner.selectedItem.toString())
                        startActivity(intent)
                    }
                })
            } else {
                if (ActivityCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            Log.e("Lastknown", location.toString())
                            geoFireHelpRequest.setLocation(fbAuth.currentUser?.uid, GeoLocation(location.latitude, location.longitude), { key, error ->
                                if (error != null) {
                                    Log.e("GEOFIRE", error.details)
                                } else {
                                    Log.e("GEOFIRE", "success")
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
                            locationProxy.requestUsersLocation()
                        }
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
        R.id.action_start_session -> {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
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
