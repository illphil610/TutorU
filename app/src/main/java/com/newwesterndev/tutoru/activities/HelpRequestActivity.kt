package com.newwesterndev.tutoru.activities

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.gms.maps.model.LatLng
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.LocationProxy
import kotlinx.android.synthetic.main.activity_help_request.*

class HelpRequestActivity : AppCompatActivity() {

    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_request)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val subjectList = ArrayList<String>()
        subjectList.add("English")
        subjectList.add("Mathematics")
        subjectList.add("History")
        subjectList.add("Computer Science")
        subjectList.add("Psychology")
        subjectList.add("Political Science")
        subjectList.add("Business")

        val spinnerJawn = findViewById<Spinner>(R.id.subject_spinner)

        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectList)
        spinnerJawn.adapter = spinnerAdapter

        val firebaseManager = FirebaseManager.instance

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        val locationProxy = LocationProxy(this, locationManager!!)
        locationProxy.requestUsersLocation()
        Log.e("LOCAL_OnCreate", locationProxy.getUsersLocation().toString())

        /*
        // This needs to be created dynamically but set as this for name
        val tutee = Model.Tutee(1, "Phil", "illphil215", true)
        // Create the H_B object after the fields have been selected and send to FB
        val helpBroadCast = Model.HelpBroadCast(tutee,
                arrayListOf(Model.Course("Calculus", "Math")),
                true,
                "what is the limit of this bitches ass?",
                System.currentTimeMillis(),
                LatLng(0.0, 0.0))
        //firebaseManager.sendHelpBroadcastRequest(helpBroadCast)
        //we need to then search for Tutors nearby and then filter by if they
        // tutor for a specific course and if they are available or not
        */
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationProxy = LocationProxy(this, locationManager!!)
        locationProxy.requestUsersLocation()
        Log.e("LOCAL", locationProxy.getUsersLocation().toString())
    }
}