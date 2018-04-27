package com.newwesterndev.tutoru.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class CourseDbHelper(context: Context): ManagedSQLiteOpenHelper(context, "currentcoursesdb") {
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(CourseSQLiteContract.TABLE_NAME, true,
                CourseSQLiteContract.COLUMN_ID to INTEGER + PRIMARY_KEY,
                CourseSQLiteContract.COLUMN_COURSE_NAME to TEXT,
                CourseSQLiteContract.COLUMN_FROM_SUBJECT_NAME to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(CourseSQLiteContract.TABLE_NAME, true)
    }

    companion object {
        private var instance: CourseDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): CourseDbHelper{
            if (instance == null) {
                instance = CourseDbHelper(context.applicationContext)
            }
            return instance!!
        }
    }
}

val Context.coursedatabase: CourseDbHelper
    get() = CourseDbHelper.getInstance(applicationContext)