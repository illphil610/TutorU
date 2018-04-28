package com.newwesterndev.tutoru.utilities

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.newwesterndev.tutoru.R

class MessageNotificationService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: $refreshedToken")

        //save to shared prefs to be used when acct is created
        val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("FCM_ID", refreshedToken)
        editor.apply()
        Log.e("SAVED FCM", refreshedToken)
    }
}