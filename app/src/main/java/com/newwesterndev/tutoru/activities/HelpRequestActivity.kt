package com.newwesterndev.tutoru.activities

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
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.LoginActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.LocationProxy
import kotlinx.android.synthetic.main.activity_help_request.*
import com.newwesterndev.tutoru.utilities.Utility

class HelpRequestActivity : AppCompatActivity() {

    private lateinit var dbManager: DbManager
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var fbManager: FirebaseManager
    private lateinit var mUtil: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_request)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

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
            var list = ArrayList<Model.Course>()
            list.add(Model.Course("Geometry", "Math"))
            fbManager.sendHelpBroadcastRequest(
                    Model.HelpBroadCast(
                    // change this!!!!! just hacking it with the ! but check this please
                            Model.Tutee(fbAuth.currentUser!!.uid, "Phil McKracken", true),
                            list, true, "I have a math question because i suck at math"))

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

    override fun onBackPressed() {
        finishAffinity()
    }
}
