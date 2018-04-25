package com.newwesterndev.tutoru.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.db.PopulateDatabase
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fbAuth = FirebaseAuth.getInstance()
    private var mUtil: Utility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUtil = Utility()

        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            } else {
                Log.e("TUTEE_ID", fbAuth?.currentUser?.uid)
            }
        }

        // This just fills the database when the app is first installed
        val dbManager = DbManager(this)
        val prefs = getSharedPreferences(Contract.DB_FIRST_APP_LAUNCH, Context.MODE_PRIVATE)
        val isDataseFilled = prefs.getString(Contract.APP_LAUNCHED, Contract.APP_HASNT_LAUNCHED)
        // This just makes sure the database is only filled once.
        if (isDataseFilled == Contract.APP_HASNT_LAUNCHED) {
            val populate = PopulateDatabase(this)
            populate.populateDataWithSubjects(dbManager)
            with (prefs.edit()) {
                putString(Contract.APP_LAUNCHED, "true")
                apply()
            }
        }

        // Just showing you how you can use the methods from dbManager to easily grab data
        val mathList = dbManager.getCourses("Mathematics")
        val compSciList = dbManager.getCourses("Computer Science")
        Log.e("MATH_LIST", mathList.toString())
        Log.e("COMP_LIST", compSciList.toString())

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
            mUtil?.showMessage(view, "Logging Out...")
            fbAuth.signOut()
            finishAffinity()
        }

        button_tutor_profile.setOnClickListener {
            val profileIntent = Intent(this, TutorProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }
}
