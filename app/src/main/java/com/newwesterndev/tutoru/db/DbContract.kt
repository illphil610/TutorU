package com.newwesterndev.tutoru.db

object SubjectSQLiteContract {
    val TABLE_NAME = "Subjects"
    val COLUMN_ID = "_id"
    val COLUMN_SUBJECT_NAME = "subjectname"
}

object CourseSQLiteContract {
    val TABLE_NAME = "Courses"
    val COLUMN_ID = "_id"
    val COLUMN_COURSE_NAME = "coursename"
    val COLUMN_FROM_SUBJECT_NAME = "fromsubject"
}