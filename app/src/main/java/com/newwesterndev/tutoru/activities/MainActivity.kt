package com.newwesterndev.tutoru.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.activities.Tutee.HelpRequestActivity
import com.newwesterndev.tutoru.activities.Tutor.TutorProfileActivity
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

        // Create the stuff we need to do things
        mUtil = Utility()
        mDbManager = DbManager(this)

        // determine if the user is authenticated or not
        fbAuth = FirebaseAuth.getInstance()
        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                // user is logged in and we support that but heres a log statement
                Log.e("USER_ID", fbAuth.currentUser?.uid)
            }
        }

        // This just fills the database when the app is first installed
        val prefs = getSharedPreferences(Contract.DB_FIRST_APP_LAUNCH, Context.MODE_PRIVATE)
        val isDataseFilled = prefs.getString(Contract.APP_LAUNCHED, Contract.APP_HASNT_LAUNCHED)
        if (isDataseFilled == Contract.APP_HASNT_LAUNCHED) {
            val populate = PopulateDatabase(this)
            populate.populateDataWithSubjects(mDbManager)
            with (prefs.edit()) {
                putString(Contract.APP_LAUNCHED, "true")
                apply()
            }
        }

        if (fbAuth.currentUser != null) {
            val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
            val user = preferences.getString(fbAuth.currentUser?.email, "unknown")
            when (user) {
                getString(R.string.tuteeMain) -> {
                    val intent = Intent(this, HelpRequestActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                getString(R.string.tutorMain) -> {
                    val intent = Intent(this, TutorProfileActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                else -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }
    }
}
