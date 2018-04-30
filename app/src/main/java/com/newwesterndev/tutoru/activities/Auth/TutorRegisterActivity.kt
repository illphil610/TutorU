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
import com.google.firebase.auth.UserProfileChangeRequest
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Tutor.TutorProfileActivity
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_tutor_register.*

class TutorRegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    private var mAuth: FirebaseAuth? = null
    private var mUtility: Utility? = null
    private val dbManager = DbManager(this)
    private var context: Context = this

    private val listOfCheckedSubjects = ArrayList<String>()
    private val listOfCheckedCourses = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_tutor_register)

        mAuth = FirebaseAuth.getInstance()
        mUtility = Utility()

        nameEditText = findViewById(R.id.edit_text_tutor_reg_name)
        emailEditText = findViewById(R.id.edit_text_tutor_reg_email)
        passwordEditText = findViewById(R.id.edit_text_tutor_reg_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_tutor_reg_confirm_password)
        submitButton = findViewById(R.id.button_tutor_reg_submit)
        cancelButton = findViewById(R.id.button_tutor_reg_cancel)

        submitButton.setOnClickListener { view ->
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if (mUtility!!.isValidEmail(email) && mUtility!!.isValidPassword(password)) {
                    if (password == confirmPassword) {
                        mUtility?.showMessage(view, "Creating your account, Mr. Tutor!")
                        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this, { task ->
                            if (task.isSuccessful) {
                                val userProfileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                mAuth?.currentUser?.updateProfile(userProfileUpdates)?.addOnCompleteListener {
                                    // save user type to shared preferences to use throughout the application
                                    val sharedPref = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
                                    with(sharedPref.edit()) {
                                        putString(mAuth?.currentUser?.email.toString(), "tutor")
                                        apply()
                                    }

                                    val fcm_id = sharedPref.getString(getString(R.string.FCM_ID), "no fcm_id")

                                    // this will include the necessary course / subject lists but for right now its nothing but blank lists
                                    FirebaseManager.instance.createTutor(Model.Tutor(mAuth?.currentUser!!.uid, "tutor", fcm_id, name, "0.0", "0", false, listOfCheckedCourses))

                                    // Send the user to the MainScreen for now
                                    val intent = Intent(this, TutorProfileActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        button_subject_course.setOnClickListener {
            val subjectsFromDB = dbManager.getSubjects()
            val subjectNames = ArrayList<String>()

            for (subject in subjectsFromDB) {
                subjectNames.add(subject.name)
            }
            openSubjectSelectDialog(subjectNames)
        }
    }

    private fun openSubjectSelectDialog(subjectList: ArrayList<String>) {
        AlertDialog.Builder(this)
            .setTitle("Select Subjects")
            .setIcon(R.mipmap.ic_books)
            .setCancelable(false)
            .setMultiChoiceItems(subjectList.toTypedArray(), null) { _, indexSelected, isChecked ->
                if (isChecked) {
                    val checkedSubject: String = subjectList[indexSelected]
                    listOfCheckedSubjects.add(checkedSubject)
                } else {
                    return@setMultiChoiceItems
                }
            }
            .setPositiveButton("Now Select Courses") { _, _ ->
                saveSubjectsToSharedPref(listOfCheckedSubjects)
                openCourseSelectDialog(listOfCheckedSubjects)
                Toast.makeText(this, "Subjects Selected", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun openCourseSelectDialog(subjects: ArrayList<String>) {
        val courseNames = ArrayList<String>()
        courseNames.clear()

        for (i in 0 until (subjects.size)) {
            val subject = subjects[i]
            val coursesFromDB = dbManager.getCourses(subject)
            for (course in coursesFromDB) {
                courseNames.add(course.name)
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Select Courses")
            .setIcon(R.mipmap.ic_books)
            .setCancelable(false)
            .setMultiChoiceItems(courseNames.toTypedArray(), null) { _, indexSelected, isChecked ->
                if (isChecked) {
                    val checkedCourse: String = courseNames[indexSelected]
                    listOfCheckedCourses.add(checkedCourse)
                } else {
                    return@setMultiChoiceItems
                }
            }
            .setPositiveButton("Ok") { _, _ ->
                saveCoursesToSharedPref(listOfCheckedCourses)
                Toast.makeText(this, "Courses Selected", Toast.LENGTH_SHORT).show()
                button_subject_course.isEnabled = false
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun saveSubjectsToSharedPref(subjects: List<String>) {
        val sharedPreferences: SharedPreferences by lazy {
            this.getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
        }
        with(sharedPreferences.edit()) {
            for (subjectIndex in 0 until subjects.size) {
                putString("Subject$subjectIndex", subjects[subjectIndex])
            }
            apply()
        }
    }

    private fun saveCoursesToSharedPref(courses: List<String>) {
        val sharedPreferences: SharedPreferences by lazy {
            this.getSharedPreferences(Contract.SHARED_PREF_COURSES, Context.MODE_PRIVATE)
        }

        with (sharedPreferences.edit()) {
            for (courseIndex in 0 until courses.size) {
                putString("Course$courseIndex", courses[courseIndex])
            }
            apply()
        }
    }

    // disables back button so the user has to click cancel (life cycle stuff)
    override fun onBackPressed() {}
}
