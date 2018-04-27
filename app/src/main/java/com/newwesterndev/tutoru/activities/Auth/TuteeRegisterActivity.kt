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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Tutee.HelpRequestActivity
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility

class TuteeRegisterActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    private lateinit var mAuth: FirebaseAuth
    private var mUtility: Utility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // set content view and find xml values variables
        setContentView(R.layout.activity_tutee_register)
        name = findViewById(R.id.edit_text_tutee_reg_name)
        email = findViewById(R.id.edit_text_tutee_reg_email)
        password = findViewById(R.id.edit_text_tutee_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutee_reg_password)
        submitButton = findViewById(R.id.button_tutee_reg_submit)
        cancelButton = findViewById(R.id.button_tutee_reg_cancel)

        mAuth = FirebaseAuth.getInstance()
        val mFirebaseManager = FirebaseManager.instance
        mUtility = Utility()

        submitButton.setOnClickListener { view ->
            if (!TextUtils.isEmpty(email.text.toString()) && !TextUtils.isEmpty(password.text.toString())
                    && !TextUtils.isEmpty(name.text.toString())) {
                mUtility?.showMessage(view, "Creating your account, Mr. Tutee!")
                mAuth.createUserWithEmailAndPassword(email.text.toString(),
                        password.text.toString()).addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        val userProfileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name.text.toString()).build()
                        mAuth.currentUser?.updateProfile(userProfileUpdates)?.addOnCompleteListener {
                            // save user type to shared preferences to use throughout the application
                            val sharedPref = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString(mAuth.currentUser?.email.toString(), "tutee")
                                apply()
                            }

                            // Create / Save the Tutee in Firebase RD
                            FirebaseManager.instance.createTutee(Model.Tutee(mAuth.currentUser?.uid!!, name.text.toString(), true))

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
