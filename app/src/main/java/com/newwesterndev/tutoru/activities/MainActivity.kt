package com.newwesterndev.tutoru.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.newwesterndev.tutoru.R
import org.jetbrains.anko.find

class MainActivity : AppCompatActivity() {

    lateinit var buttonMap: Button
    lateinit var buttonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonMap = findViewById(R.id.button2)
        buttonMap.setOnClickListener { startMap() }

        buttonLogin = findViewById(R.id.button3)
        buttonLogin.setOnClickListener { startLogin() }

    }

    private fun startMap() {
        val mapIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapIntent)
    }

    private fun startLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}
