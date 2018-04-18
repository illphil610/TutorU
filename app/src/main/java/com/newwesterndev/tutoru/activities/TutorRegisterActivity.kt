package com.newwesterndev.tutoru.activities

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.newwesterndev.tutoru.R

class TutorRegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var subjectCourseButton: Button
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_register)

        email = findViewById(R.id.edit_text_tutor_reg_email)
        password = findViewById(R.id.edit_text_tutor_reg_password)
        confirmPassword = findViewById(R.id.edit_text_tutor_reg_password)
        subjectCourseButton = findViewById(R.id.button_subject_course)
        submitButton = findViewById(R.id.button_tutor_reg_submit)
        cancelButton = findViewById(R.id.button_tutor_reg_cancel)

        subjectCourseButton.setOnClickListener { showSubjectCourseSelectionAlertDialog() }
    }

    private fun showSubjectCourseSelectionAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Select Subjects")
        dialogBuilder.setIcon(R.mipmap.ic_books)

        dialogBuilder.setPositiveButton("Now Select Courses") {dialog, which ->
            Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
        }

        dialogBuilder.setNegativeButton("Cancel") {dialog, which ->
            Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
        }

        dialogBuilder.create().show()
    }
}
