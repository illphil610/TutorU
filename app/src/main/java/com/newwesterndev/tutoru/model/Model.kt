package com.newwesterndev.tutoru.model

class Model {
    data class Subject(var name: String)

    data class Course(var name: String,
                      var fromSubjectName: String)
}