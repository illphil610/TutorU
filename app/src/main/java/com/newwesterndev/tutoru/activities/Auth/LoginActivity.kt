package com.newwesterndev.tutoru.activities.Auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.MainActivity
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var fbAuth: FirebaseAuth? = null
    private var mUtil: Utility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_login)

        fbAuth = FirebaseAuth.getInstance()
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
        dialogBuilder.setView(dialogView)

        val tutorButton = dialogView.findViewById(R.id.button_dialog_tutor) as Button
        val tuteeButton = dialogView.findViewById(R.id.button_dialog_tutee) as Button

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

    private fun signIn(view: View, email: String, password: String) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mUtil?.showMessage(view, "Authenticating...")
            fbAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this, { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email", fbAuth?.currentUser?.email)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    mUtil?.showMessage(view, "Error: ${task.exception?.message}")
                }
            })
        }
    }

    // disables back button
    override fun onBackPressed() {
        finish()
    }
}
