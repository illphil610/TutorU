package com.newwesterndev.tutoru.activities

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.LocationProxy

class HelpRequestActivity : AppCompatActivity() {

    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_request)

        val firebaseManager = FirebaseManager.instance
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProxy = LocationProxy(this, locationManager!!)
        locationProxy.getCurrentUsersLocation()

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
    }
}
