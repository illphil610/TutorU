package com.newwesterndev.tutoru.model

class SQLModel {
    data class Subject(var id: Int,
                       var name: String)

    data class Course(var id: Int,
                      var name: String,
                      var fromSubjectName: String)
}