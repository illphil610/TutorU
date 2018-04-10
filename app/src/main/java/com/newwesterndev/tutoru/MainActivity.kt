package com.newwesterndev.tutoru

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button2)
        button.setOnClickListener { startMap() }

    }

    fun startMap() {
        val mapIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapIntent)
    }
}
