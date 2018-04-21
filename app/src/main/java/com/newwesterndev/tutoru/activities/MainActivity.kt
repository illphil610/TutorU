package com.newwesterndev.tutoru.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.newwesterndev.tutoru.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button2)
        button.setOnClickListener { startMap() }

        // Just a temp solution to get to the HelpRequestActivity
        helpBroadcastButton.setOnClickListener {
            val intent = Intent(this, HelpRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startMap() {
        val mapIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapIntent)
    }
}
