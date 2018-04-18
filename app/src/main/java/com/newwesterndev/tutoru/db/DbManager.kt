package com.newwesterndev.tutoru.db

import android.content.Context
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.model.SQLModel
import org.jetbrains.anko.db.*

class DbManager(private val context: Context) {
    private val subjectDb = SubjectDbHelper.getInstance(context)
    private val courseDb = CourseDbHelper.getInstance(context)

    fun subjectToDb(subject: Model.Subject, courses: ArrayList<Model.Course>) {
        subjectDb.use {
            createTable(SubjectSQLiteContract.TABLE_NAME, true,
                    SubjectSQLiteContract.COLUMN_ID to INTEGER + PRIMARY_KEY,
                    SubjectSQLiteContract.COLUMN_SUBJECT_NAME to TEXT)
            insert(SubjectSQLiteContract.TABLE_NAME,
                    SubjectSQLiteContract.COLUMN_SUBJECT_NAME to subject.name)
        }

        courseDb.use {
            createTable(CourseSQLiteContract.TABLE_NAME, true,
                    CourseSQLiteContract.COLUMN_ID to INTEGER + PRIMARY_KEY,
                    CourseSQLiteContract.COLUMN_COURSE_NAME to TEXT,
                    CourseSQLiteContract.COLUMN_FROM_SUBJECT_NAME to TEXT)

            for (course: Model.Course? in courses) {
                insert(CourseSQLiteContract.TABLE_NAME,
                        CourseSQLiteContract.COLUMN_COURSE_NAME to course?.name,
                        CourseSQLiteContract.COLUMN_FROM_SUBJECT_NAME to subject.name)
            }
        }
    }

    fun getSubjects(): ArrayList<Model.Subject> {
        val rowParser = classParser<SQLModel.Subject>()
        var subjectList: List<SQLModel.Subject> = ArrayList()
        val subjectModel: ArrayList<Model.Subject> = ArrayList()

        subjectDb.use {
            subjectList = select(SubjectSQLiteContract.TABLE_NAME)
                    .parseList(rowParser)
        }
        for (i in 0 until(subjectList.size)) {
            subjectModel.add(Model.Subject(subjectList[i].name))
        }
        return subjectModel
    }

    fun getCourses(subjectName: String): ArrayList<Model.Course> {
        val rowParser = classParser<SQLModel.Course>()
        var courseList: List<SQLModel.Course> = ArrayList()
        val courseModel: ArrayList<Model.Course> = ArrayList()

        courseDb.use {
            courseList = select(CourseSQLiteContract.TABLE_NAME)
                    .whereSimple("(fromsubject = ?)", subjectName)
                    .parseList(rowParser)
        }
        for (i in 0 until(courseList.size)) {
            val currentCourse = courseList[i]
            courseModel.add(Model.Course(currentCourse.name,
                    currentCourse.fromSubjectName))
        }
        return courseModel
    }
}