package com.newwesterndev.tutoru.activities

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.db.PopulateDatabase
import com.newwesterndev.tutoru.model.Contract
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        fbAuth.addAuthStateListener {
            if (fbAuth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(loginIntent)
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
        Log.e("MATH_IST", mathList.toString())
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
            showMessage(view, "Logging Out...")
            fbAuth.signOut()
        }
    }

    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}
