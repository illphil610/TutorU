package com.newwesterndev.tutoru.model

import com.google.android.gms.maps.model.LatLng
import java.util.*

class Model {
    data class User(var id: Int,
                    var name: String,
                    var mUserName: String,
                    var locaton: LatLng)

    data class Tutor(var id: Int,
                var name: String,
                var mUserName: String,
                var isAvailable: Boolean)

    data class Tutee(var id: Int,
                     var name: String,
                     var mUserName: String,
                     var requestingHelp: Boolean,
                     var subjectRequested: Course)

    data class HelpBroadCast(var tutee: Model.Tutee,
                             var courseList: ArrayList<Course>,
                             var stillAwaitingHelp: Boolean,
                             var date: Date)

    data class TutorSession(var course: Course,
                            var tutor: Tutor,
                            var tutee: Tutee,
                            var rating: Int,
                            var timeElapsed: Long)

    data class Subject(var name: String)

    data class Course(var name: String,
                      var fromSubjectName: String)

    data class ChatMessage(var messageText: String,
                           var messageFromUser: String,
                           var messageTime: Long)
}