package com.newwesterndev.tutoru.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.newwesterndev.tutoru.R
import kotlinx.android.synthetic.main.activity_main.*


import org.jetbrains.anko.find


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Just a temp solution to get to the HelpRequestActivity
        helpBroadcastButton.setOnClickListener {
            val intent = Intent(this, HelpRequestActivity::class.java)
            startActivity(intent)
        }
    }
        buttonMap.setOnClickListener {
            val mapIntent = Intent(this, MapsActivity::class.java)
            startActivity(mapIntent)
        }

        buttonLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}
