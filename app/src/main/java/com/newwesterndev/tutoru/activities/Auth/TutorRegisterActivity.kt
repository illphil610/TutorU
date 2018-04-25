package com.newwesterndev.tutoru.activities.Auth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.MainActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility

class TutorRegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var subjectCourseButton: Button
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    private var mAuth: FirebaseAuth? = null
    private var mUtility: Utility? = null
    private val dbManager = DbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_tutor_register)

        mAuth = FirebaseAuth.getInstance()
        mUtility = Utility()

        email = findViewById(R.id.edit_text_tutor_reg_email)
        password = findViewById(R.id.edit_text_tutor_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutor_reg_password)
        subjectCourseButton = findViewById(R.id.button_subject_course)
        submitButton = findViewById(R.id.button_tutor_reg_submit)
        cancelButton = findViewById(R.id.button_tutor_reg_cancel)
        val name = findViewById<EditText>(R.id.edit_text_tutor_reg_name)

        subjectCourseButton.setOnClickListener { openSubjectSelectDialog() }

        submitButton.setOnClickListener { view ->
            if (!TextUtils.isEmpty(email.text.toString()) && !TextUtils.isEmpty(password.text.toString())) {
                mUtility?.showMessage(view, "Creating your account, Mr. Tutor!")
                mAuth?.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())?.addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        // this will include the necessary course / subject lists but for right now its nothing but blank lists
                        FirebaseManager.instance.createTutor(Model.Tutor(name.text.toString(), false, ArrayList(), ArrayList()))

                        // Send the user to the MainScreen for now
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent)
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

    private fun openSubjectSelectDialog() {
        val subjectsFromDB = dbManager.getSubjects()
        val subjectNames = ArrayList<String>()

        for (subject in subjectsFromDB) {
            subjectNames.add(subject.name)
        }

        AlertDialog.Builder(this)
            .setTitle("Select Subjects")
            .setIcon(R.mipmap.ic_books)
            .setMultiChoiceItems(subjectNames.toTypedArray(), null) { _, indexSelected, isChecked ->
                val checkedSubject: String = subjectNames[indexSelected]

                if (isChecked) {
                    Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show()

                    val sharedPreferences: SharedPreferences by lazy {
                        this.getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
                    }
                    val editor = sharedPreferences.edit()
                    editor.putString("subject", checkedSubject)
                    editor.apply()

//                    val fromPref = sharedPreferences.getString("subject", checkedSubject)
//                    println(fromPref)

                } else {
                    return@setMultiChoiceItems
                }
            }
            .setPositiveButton("Now Select Courses") { _, _ ->
                Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
//                openCourseSelectDialog()

            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
            }
            .create()
            .show()
    }

    private fun openCourseSelectDialog(listOfCourses: ArrayList<String>) {
//        Pass in items selected from subject selection dialog to getCourses
//        val coursesFromDB = dbManager.getCourses(checkedItems)
        val courseNames = ArrayList<String>()

//        for (course in coursesFromDB) {
//            courseNames.add(course.name)
//        }


        AlertDialog.Builder(this)
            .setTitle("Select Courses")
            .setIcon(R.mipmap.ic_books)
            .setMultiChoiceItems(courseNames.toTypedArray(), null) { dialogInterface, indexSelected, isChecked ->
                if (isChecked) {

                } else {

                }
            }
            .setPositiveButton("Ok") { _, _ ->
                Toast.makeText(this, "Courses Selected", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
            }
            .create()
            .show()
    }

    // disables back button so the user has to click cancel (life cycle stuff)
    override fun onBackPressed() {}
}
