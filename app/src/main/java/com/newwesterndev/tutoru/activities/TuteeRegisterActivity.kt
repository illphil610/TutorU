package com.newwesterndev.tutoru.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.newwesterndev.tutoru.R

class TuteeRegisterActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutee_register)

        email = findViewById(R.id.edit_text_tutee_reg_email)
        password = findViewById(R.id.edit_text_tutee_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutee_reg_password)
        submitButton = findViewById(R.id.button_tutee_reg_submit)
        cancelButton = findViewById(R.id.button_tutee_reg_cancel)
    }
}
