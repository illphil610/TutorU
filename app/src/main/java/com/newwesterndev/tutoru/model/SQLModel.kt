package com.newwesterndev.tutoru.model

class SQLModel {
    data class Subject(var name: String)

    data class Course(var name: String,
                      var fromSubjectName: String)
}