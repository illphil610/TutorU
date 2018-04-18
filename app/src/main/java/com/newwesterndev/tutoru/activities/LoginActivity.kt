package com.newwesterndev.tutoru.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.newwesterndev.tutoru.R

class LoginActivity : AppCompatActivity() {
    private lateinit var tutorButton: Button
    private lateinit var tuteeButton: Button
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.edit_text_signin_email)
        password = findViewById(R.id.edit_text_signin_password)
        loginButton = findViewById(R.id.button_login)
        signupButton = findViewById(R.id.button_signup)

        signupButton.setOnClickListener {
            showRegisterAlertDialog()
        }
    }

    private fun showRegisterAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_register_dialog, null)
        dialogBuilder.setView(dialogView)

        tutorButton = dialogView.findViewById(R.id.button_dialog_tutor) as Button
        tuteeButton = dialogView.findViewById(R.id.button_dialog_tutee) as Button

        tutorButton.setOnClickListener {
            val tutorIntent = Intent(this, TutorRegisterActivity::class.java)
            startActivity(tutorIntent)
        }

        tuteeButton.setOnClickListener {
            val tuteeIntent = Intent(this, TuteeRegisterActivity::class.java)
            startActivity(tuteeIntent)
        }

        dialogBuilder.create().show()
    }
}
