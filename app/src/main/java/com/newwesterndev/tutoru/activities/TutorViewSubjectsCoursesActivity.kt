package com.newwesterndev.tutoru.activities

import android.app.AlertDialog
import android.app.ListActivity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import kotlinx.android.synthetic.main.activity_tutor_view_subjects_courses.*

class TutorViewSubjectsCoursesActivity : AppCompatActivity() {
    private val dbManager = DbManager(this)
    private val listOfCheckedSubjects = ArrayList<String>()
    private val listOfCheckedCourses = ArrayList<String>()
    private val subjectListFromPref = ArrayList<String>()
    private val courseListFromPref = ArrayList<String>()
    private var subjectListAdapter: ArrayAdapter<String>? = null
    private var courseListAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_view_subjects_courses)
        setSupportActionBar(toolbar)

        val subjectListView = findViewById<ListView>(R.id.listview_subjects)
        val courseListView = findViewById<ListView>(R.id.listview_courses)

        //Subjects
        subjectListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subjectListFromPref)
        subjectListView.adapter = subjectListAdapter

        val sharedPreferencesSubjects = getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
        val subjectsFromPrefMap = sharedPreferencesSubjects.all

        for (entry in subjectsFromPrefMap) {
            subjectListFromPref.add(entry.value.toString())
            println(entry)
        }

        subjectListAdapter!!.notifyDataSetChanged()

        //Courses
        courseListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, subjectListFromPref)
        courseListView.adapter = subjectListAdapter

        val sharedPreferencesCourses = getSharedPreferences(Contract.SHARED_PREF_COURSES, Context.MODE_PRIVATE)
        val coursesFromPrefMap = sharedPreferencesCourses.all

        for (entry in coursesFromPrefMap) {
            courseListFromPref.add(entry.value.toString())
            println(entry)
        }

        fab.setOnClickListener { openSubjectSelectDialog() }
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

                    if (isChecked) {
                        val checkedSubject: String = subjectNames[indexSelected]
                        listOfCheckedSubjects.add(checkedSubject)

                        val sharedPreferences: SharedPreferences by lazy {
                            this.getSharedPreferences(Contract.SHARED_PREF_SUBJECTS, Context.MODE_PRIVATE)
                        }

                        with (sharedPreferences.edit()) {
                            putString(checkedSubject, checkedSubject)
                            apply()
                        }

                        val fromPref = sharedPreferences.getString(checkedSubject, checkedSubject)
                        println(fromPref)

                    } else {
                        return@setMultiChoiceItems
                    }
                }
                .setPositiveButton("Now Select Courses") { _, _ ->
                    Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
                    openCourseSelectDialog(listOfCheckedSubjects)

                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
                    finish()
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

                        val sharedPreferences: SharedPreferences by lazy {
                            this.getSharedPreferences(Contract.SHARED_PREF_COURSES, Context.MODE_PRIVATE)
                        }

                        with (sharedPreferences.edit()) {
                            putString(checkedCourse, checkedCourse)
                            apply()
                        }

                        val fromPref = sharedPreferences.getString(checkedCourse, checkedCourse)
                        println(fromPref)

                    } else {
                        return@setMultiChoiceItems
                    }
                }
                .setPositiveButton("Ok") { _, _ ->
                    Toast.makeText(this, "Courses Selected", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .create()
                .show()
    }
}