package com.newwesterndev.tutoru.activities.Auth

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.HelpRequestActivity
import com.newwesterndev.tutoru.activities.TutorProfileActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.db.PopulateDatabase
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity() {

    private lateinit var fbAuth: FirebaseAuth
    private lateinit var mUtil: Utility
    private lateinit var mDbManager: DbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_login)

        fbAuth = FirebaseAuth.getInstance()
        mDbManager = DbManager(this)
        mUtil = Utility()

        button_login.setOnClickListener { view ->
            signIn(view, edit_text_signin_email.text.toString(), edit_text_signin_password.text.toString())
        }

        signupButton.setOnClickListener {
            showRegisterAlertDialog()
        }
    }

    private fun showRegisterAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_register_dialog, null)
        val createdDialogView = dialogBuilder.create()
        createdDialogView.setView(dialogView)

        val tutorButton = dialogView.findViewById(R.id.button_dialog_tutor) as Button
        val tuteeButton = dialogView.findViewById(R.id.button_dialog_tutee) as Button

        tutorButton.setOnClickListener {
            val tutorIntent = Intent(this, TutorRegisterActivity::class.java)
            startActivity(tutorIntent)
            createdDialogView.dismiss()
            finish()
        }

        tuteeButton.setOnClickListener {
            val tuteeIntent = Intent(this, TuteeRegisterActivity::class.java)
            startActivity(tuteeIntent)
            createdDialogView.dismiss()
            finish()
        }
        createdDialogView.show()
    }

    private fun signIn(view: View, email: String, password: String) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mUtil.showMessage(view, "Authenticating...")
            fbAuth.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this, { task ->
                if (task.isSuccessful) {
                    // check the type of user and route to either HelpBroadcast or TutorProfile
                    val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
                    val user = preferences.getString("user_type", "unknown")

                    when (user) {
                        "tutee" -> {
                            val intent = Intent(this, HelpRequestActivity::class.java)
                            intent.putExtra("email", fbAuth.currentUser?.email)
                            startActivity(intent)
                            finishAffinity()
                        }
                        "tutor" -> {
                            val intent = Intent(this, TutorProfileActivity::class.java)
                            intent.putExtra("email", fbAuth.currentUser?.email)
                            startActivity(intent)
                            finishAffinity()
                        }
                        else -> {
                            // Sending the default users here to the activity im working on ;)
                            // This is a bug tho... im only saving the user type when they create the profile
                            // so if they uninstall and reinstall the app it wont know what type they are.  Working on a
                            // solution but this felt okay for now.
                            val intent = Intent(this, HelpRequestActivity::class.java)
                            intent.putExtra("email", fbAuth.currentUser?.email)
                            startActivity(intent)
                            finishAffinity()
                        }
                    }
                } else {
                    mUtil.showMessage(view, "Error: ${task.exception?.message}")
                }
            })
        }
    }

    // disables back button
    override fun onBackPressed() {
        finishAffinity()
    }
}
