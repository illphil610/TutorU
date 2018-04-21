package com.newwesterndev.tutoru.utilities

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.firebase.geofire.GeoFire
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng

class LocationProxy(val context: Context, val locationManager: LocationManager)  {

    private lateinit var geoFire: GeoFire
    private var mLocation: Location? = null

    private var locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(10.toFloat())

    @Throws(SecurityException::class)
    fun requestUsersLocation() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.e("LOCAL", location.latitude.toString() + " " + location.longitude.toString())
            mLocation = Location(location)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun getUsersLocation(): Location? {
        return mLocation
    }
}