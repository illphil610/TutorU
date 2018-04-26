package com.newwesterndev.tutoru.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.db.PopulateDatabase
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var mDbManager: DbManager
    private lateinit var mUtil: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbAuth = FirebaseAuth.getInstance()
        mDbManager = DbManager(this)
        mUtil = Utility()

        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                //finishAffinity()
            } else {
                Log.e("USER_ID", fbAuth.currentUser?.uid)
            }
        }

        // This just fills the database when the app is first installed
        val prefs = getSharedPreferences(Contract.DB_FIRST_APP_LAUNCH, Context.MODE_PRIVATE)
        val isDataseFilled = prefs.getString(Contract.APP_LAUNCHED, Contract.APP_HASNT_LAUNCHED)

        // This just makes sure the database is only filled once.
        if (isDataseFilled == Contract.APP_HASNT_LAUNCHED) {
            val populate = PopulateDatabase(this)
            populate.populateDataWithSubjects(mDbManager)
            with (prefs.edit()) {
                putString(Contract.APP_LAUNCHED, "true")
                apply()
            }
        }
        Log.e("COURSE", mDbManager.getSubjects().toString())

        //check if the user is a tutor/tutee and route accordingly
        // check the type of user and route to either HelpBroadcast or TutorProfile
        val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
        val user = preferences.getString(fbAuth.currentUser?.email, "unknown")
        when (user) {
            "tutee" -> {
                val intent = Intent(this, HelpRequestActivity::class.java)
                intent.putExtra("email", fbAuth.currentUser?.email)
                startActivity(intent)
                finishAffinity()
            }
            "tutor" -> {
                val intent = Intent(this, TutorProfileActivity::class.java)
                intent.putExtra("email", fbAuth.currentUser?.email)
                startActivity(intent)
                finishAffinity()
            }
            else -> {
                val intent = Intent(this, HelpRequestActivity::class.java)
                intent.putExtra("email", fbAuth.currentUser?.email)
                startActivity(intent)
                finishAffinity()
            }
        }

        // Just a temp solution to get to the HelpRequestActivity
        helpButton.setOnClickListener {
            val intent = Intent(this, HelpRequestActivity::class.java)
            startActivity(intent)
        }

        buttonMap.setOnClickListener {
            val mapIntent = Intent(this, MapsActivity::class.java)
            startActivity(mapIntent)
        }

        sign_out_button.setOnClickListener { view ->
            mUtil.showMessage(view, "Logging Out...")
            fbAuth.signOut()
            finishAffinity()
        }

        tutor_profile_button.setOnClickListener {
            val tutorProfileIntent = Intent(this, TutorProfileActivity::class.java)
            startActivity(tutorProfileIntent)
        }
    }
}
