package com.newwesterndev.tutoru.db

import android.content.Context
import android.graphics.ColorSpace
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.model.SQLModel

class PopulateDatabase(context: Context) {
    fun populateDataWithSubjects(dbManager: DbManager) {
        val subjects = ArrayList<Model.Subject>()

        // Just declaring some subjects but we can easily add to them here
        subjects.add(Model.Subject("English"))
        subjects.add(Model.Subject("Mathematics"))
        subjects.add(Model.Subject("Computer Science"))
        subjects.add(Model.Subject("Business"))

        // Math
        val mathCourses = ArrayList<Model.Course>()
        mathCourses.add(Model.Course("Calculus", "Mathematics"))
        mathCourses.add(Model.Course("Geometry", "Mathematics"))
        mathCourses.add(Model.Course("Algebra", "Mathematics"))
        mathCourses.add(Model.Course("Discrete", "Mathematics"))
        mathCourses.add(Model.Course("Probability", "Mathematics"))

        // English
        val englishCourses = ArrayList<Model.Course>()
        englishCourses.add(Model.Course("Technical Writing", "English"))
        englishCourses.add(Model.Course("Writing Composition", "English"))
        englishCourses.add(Model.Course("Reading Comprehension", "English"))

        // Computer Science
        val compSciCourses = ArrayList<Model.Course>()
        compSciCourses.add(Model.Course("Basic/Intermediate Java", "Computer Science"))
        compSciCourses.add(Model.Course("Software Design", "Computer Science"))
        compSciCourses.add(Model.Course("Programming in C", "Computer Science"))
        compSciCourses.add(Model.Course("Operating Systems", "Computer Science"))
        compSciCourses.add(Model.Course("Data Structures and Algorithms", "Computer Science"))
        compSciCourses.add(Model.Course("Python Programming", "Computer Science"))

        // This is saving the above lists to our Anko SQLite database
        dbManager.subjectToDb(Model.Subject("Mathematics"), mathCourses)
        dbManager.subjectToDb(Model.Subject("English"), englishCourses)
        dbManager.subjectToDb(Model.Subject("Computer Science"), compSciCourses)
    }
}