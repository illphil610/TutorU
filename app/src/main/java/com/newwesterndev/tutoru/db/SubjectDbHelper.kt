package com.newwesterndev.tutoru.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class SubjectDbHelper(context: Context): ManagedSQLiteOpenHelper(context, "currentsubjectdb") {
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(SubjectSQLiteContract.TABLE_NAME, true,
                SubjectSQLiteContract.COLUMN_ID to INTEGER + PRIMARY_KEY,
                SubjectSQLiteContract.COLUMN_SUBJECT_NAME to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(SubjectSQLiteContract.TABLE_NAME, true)
    }

    companion object {
        private var instance: SubjectDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): SubjectDbHelper {
            if (instance == null) {
                instance = SubjectDbHelper(context.applicationContext)
            }
            return instance!!
        }
    }
}

val Context.subjectdatabase: SubjectDbHelper
    get() = SubjectDbHelper.getInstance(applicationContext)