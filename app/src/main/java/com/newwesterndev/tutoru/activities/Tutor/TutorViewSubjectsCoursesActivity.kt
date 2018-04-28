package com.newwesterndev.tutoru.activities.Tutor

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.db.DbManager
import com.newwesterndev.tutoru.model.Contract
import kotlinx.android.synthetic.main.fab_layout.*

class TutorViewSubjectsCoursesActivity : AppCompatActivity() {
    private val dbManager = DbManager(this)
    private val listOfCheckedSubjects = ArrayList<String>()
    private val listOfCheckedCourses = ArrayList<String>()
    private val courseListFromPref = ArrayList<String>()
    private var courseListAdapter: ArrayAdapter<String>? = null
    private var courseListView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_view_subjects_courses)

        courseListView = findViewById(R.id.listview_courses)

        populateCourseListView()

        fab_add.setOnClickListener {
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
                    Toast.makeText(this, "Subjects Selected", Toast.LENGTH_LONG).show()
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
                    } else {
                        return@setMultiChoiceItems
                    }
                }
                .setPositiveButton("Ok") { _, _ ->
                    saveCoursesToSharedPref(listOfCheckedCourses)
                    Toast.makeText(this, "Courses Selected", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                    finish()
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
                putString("Subject" + (subjects.size + 1), subjects[subjectIndex])
            }
            apply()
        }
    }

    private fun saveCoursesToSharedPref(courses: List<String>) {
        val sharedPreferences: SharedPreferences by lazy {
            this.getSharedPreferences(Contract.SHARED_PREF_COURSES, Context.MODE_PRIVATE)
        }

        val count = sharedPreferences.all.size

        with (sharedPreferences.edit()) {
            for (courseIndex in 0 until courses.size) {
                putString("Course" + (count + 1), courses[courseIndex])
            }
            apply()
        }
    }

    private fun populateCourseListView() {
        val sharedPreferencesCourses = getSharedPreferences(Contract.SHARED_PREF_COURSES, Context.MODE_PRIVATE)
        val coursesFromPrefMap = sharedPreferencesCourses.all

        for (entry in coursesFromPrefMap) {
            courseListFromPref.add(entry.value.toString())
        }

        courseListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, courseListFromPref)
        courseListView!!.adapter = courseListAdapter

        courseListAdapter!!.notifyDataSetChanged()
    }
}
