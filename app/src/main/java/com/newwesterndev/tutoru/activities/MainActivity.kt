package com.newwesterndev.tutoru.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.db.PopulateDatabase
import com.newwesterndev.tutoru.model.Contract
import kotlinx.android.synthetic.main.activity_main.*

import org.jetbrains.anko.find


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This just fills the database when the app is first installed
        val dbManager = DbManager(this)
        val prefs = getSharedPreferences(Contract.DB_FIRST_APP_LAUNCH, Context.MODE_PRIVATE)
        val isDataseFilled = prefs.getString(Contract.APP_LAUNCHED, Contract.APP_HASNT_LAUNCHED)

        // This just makes sure the database is only filled once.
        if (isDataseFilled == "appHasntLaunchedYet") {
            val populate = PopulateDatabase(this)
            populate.populateDataWithSubjects(dbManager)
            with (prefs.edit()) {
                putString("appLaunched", "true")
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
    }
}
