package com.newwesterndev.tutoru.activities.Auth

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Tutee.HelpRequestActivity
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility

class TuteeRegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    private lateinit var mAuth: FirebaseAuth
    private var mUtility: Utility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mAuth = FirebaseAuth.getInstance()
        val mFirebaseManager = FirebaseManager.instance
        mUtility = Utility()

        // set content view and find xml values variables
        setContentView(R.layout.activity_tutee_register)
        nameEditText = findViewById(R.id.edit_text_tutee_reg_name)
        emailEditText = findViewById(R.id.edit_text_tutee_reg_email)
        passwordEditText = findViewById(R.id.edit_text_tutee_reg_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_tutee_reg_confirm_password)
        submitButton = findViewById(R.id.button_tutee_reg_submit)
        cancelButton = findViewById(R.id.button_tutee_reg_cancel)

        submitButton.setOnClickListener { view ->
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                if (mUtility!!.isValidEmail(email) && mUtility!!.isValidPassword(password)) {
                    if(password == confirmPassword) {
                        mUtility?.showMessage(view, "Creating your account, Mr. Tutee!")
                        mAuth.createUserWithEmailAndPassword(email,
                                password).addOnCompleteListener(this, { task ->
                            if (task.isSuccessful) {
                                val userProfileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                mAuth.currentUser?.updateProfile(userProfileUpdates)?.addOnCompleteListener {
                                    // save user type to shared preferences to use throughout the application
                                    val sharedPref = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
                                    with(sharedPref.edit()) {
                                        putString(mAuth.currentUser?.email.toString(), "tutee")
                                        apply()
                                    }

                            val fcm_id = sharedPref.getString(getString(R.string.FCM_ID), "no fcm_id")

                            // Create / Save the Tutee in Firebase RD
                            FirebaseManager.instance.createTutee(Model.Tutee(mAuth.currentUser?.uid!!, "tutee", fcm_id, name, "0.0", "0", true))

                                    // Send the user to the MainScreen for now
                                    val intent = Intent(this, HelpRequestActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                mUtility?.showMessage(view, task.exception.toString())
                                Log.e("Sign up error", task.exception.toString())
                            }
                        })
                    } else {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
                    }
                } else if (!mUtility!!.isValidEmail(email)) {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_LONG).show()
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    // disables back button so the user has to click cancel (life cycle stuff)
    override fun onBackPressed() {}
}
