package com.newwesterndev.tutoru.activities.Auth

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.newwesterndev.tutoru.activities.TutorProfileActivity
import com.newwesterndev.tutoru.db.subjectdatabase
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_help_request.*
import kotlinx.android.synthetic.main.activity_tutor_register.*

class TutorRegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    private var mAuth: FirebaseAuth? = null
    private var mUtility: Utility? = null
    private val dbManager = DbManager(this)

    private val listOfCheckedSubjects = ArrayList<String>()
    private val listOfCheckedCourses = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_tutor_register)

        mAuth = FirebaseAuth.getInstance()
        mUtility = Utility()

        email = findViewById(R.id.edit_text_tutor_reg_email)
        password = findViewById(R.id.edit_text_tutor_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutor_reg_password)
        submitButton = findViewById(R.id.button_tutor_reg_submit)
        cancelButton = findViewById(R.id.button_tutor_reg_cancel)
        val name = findViewById<EditText>(R.id.edit_text_tutor_reg_name)

        submitButton.setOnClickListener { view ->
            if (!TextUtils.isEmpty(email.text.toString()) && !TextUtils.isEmpty(password.text.toString())) {
                mUtility?.showMessage(view, "Creating your account, Mr. Tutor!")
                mAuth?.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())?.addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {

                        // save user type to shared preferences to use throughout the application
                        val sharedPref = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString(mAuth?.currentUser?.email.toString(), "tutor")
                            apply()
                        }

                        // this will include the necessary course / subject lists but for right now its nothing but blank lists
                        FirebaseManager.instance.createTutor(Model.Tutor(mAuth?.currentUser!!.uid, name.text.toString(), false))

                        // Send the user to the MainScreen for now
                        val intent = Intent(this, TutorProfileActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent)
                        finish()
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
                    println("CHECKED $checkedSubject")
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

        println("CHECKED SUBJECTS " + listOfCheckedSubjects.toString())
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

        println("CHECKED COURSES " + listOfCheckedSubjects.toString())
    }

    private fun saveSubjectsToSharedPref(subjects: List<String>) {
        val sharedPreferences: SharedPreferences by lazy {
            this.getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
        }
        with(sharedPreferences.edit()) {
            for (subjectIndex in 0 until subjects.size) {
                putString("Subject$subjectIndex", subjects[subjectIndex])
                println("SAVED SUBJECT " + subjects[subjectIndex])
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
                println("SAVED COURSE " + courses[courseIndex])
            }
            apply()
        }
    }

    // disables back button so the user has to click cancel (life cycle stuff)
    override fun onBackPressed() {}
}
