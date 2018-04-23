package com.newwesterndev.tutoru.utilities

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.newwesterndev.tutoru.R

class SubjectCourseSelection {
    private lateinit var context: Context
    private val subjects = arrayOf(" Programming ", " Math ", " Science ", " Writing ")
    private val selectedSubjects = arrayListOf<Int>()
    private val courses = arrayOf(" Java 1 ", " Calculus 1 ", " Biology ", " Technical Writing ")
    private val selectedCourses = arrayListOf<Int>()

    fun openSubjectSelectDialog() {
        AlertDialog.Builder(context)
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
                    Toast.makeText(context, "Subjects Selected", Toast.LENGTH_LONG).show()
                    openCourseSelectDialog()

                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show()
                }
                .create()
                .show()
    }

    private fun openCourseSelectDialog() {
        AlertDialog.Builder(context)
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
                    Toast.makeText(context, "Courses Selected", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(context, "Canceled", Toast.LENGTH_LONG).show()
                }
                .create()
                .show()
    }
}