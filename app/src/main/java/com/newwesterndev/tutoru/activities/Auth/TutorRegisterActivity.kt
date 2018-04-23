package com.newwesterndev.tutoru.activities.Auth

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.utilities.SubjectCourseSelection
import javax.security.auth.Subject

class TutorRegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var subjectCourseButton: Button
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    //Temporary static selection items
    private val subjects = arrayOf(" Programming ", " Math ", " Science ", " Writing ")
    private val selectedSubjects = arrayListOf<Int>()
    private val courses = arrayOf(" Java 1 ", " Calculus 1 ", " Biology ", " Technical Writing ")
    private val selectedCourses = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_register)

        email = findViewById(R.id.edit_text_tutor_reg_email)
        password = findViewById(R.id.edit_text_tutor_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutor_reg_password)
        subjectCourseButton = findViewById(R.id.button_subject_course)
        submitButton = findViewById(R.id.button_tutor_reg_submit)
        cancelButton = findViewById(R.id.button_tutor_reg_cancel)

        subjectCourseButton.setOnClickListener { openSubjectSelectDialog() }
    }

    private fun openSubjectSelectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Subjects")
            .setIcon(R.mipmap.ic_books)
            .setMultiChoiceItems(subjects, null) { _, indexSelected, isChecked ->
                if (isChecked) {
                    selectedSubjects.add(indexSelected)
                } else if (selectedSubjects.contains(indexSelected)) {
                    selectedSubjects.remove(Integer.valueOf(indexSelected))
                }
            }
            .setPositiveButton("Now Select Courses") { _, _ ->
                Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
                openCourseSelectDialog()

            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
            }
            .create()
            .show()
    }

    private fun openCourseSelectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Courses")
            .setIcon(R.mipmap.ic_books)
            .setMultiChoiceItems(courses, null) { _, indexSelected, isChecked ->
                if (isChecked) {
                    selectedCourses.add(indexSelected)
                } else if (selectedCourses.contains(indexSelected)) {
                    selectedCourses.remove(Integer.valueOf(indexSelected))
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
}
