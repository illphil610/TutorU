package com.newwesterndev.tutoru.model

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Model {
    data class User(var id: Int,
                    var name: String,
                    var userName: String,
                    var locaton: LatLng)

    data class Tutor(var uid: String,
                     var acctType: String,
                     var fcm_id: String,
                     var name: String,
                     var ratingAvg: String,
                     var numOfRatings: String,
                     var isAvailable: Boolean,
                     var courseList : ArrayList<String>)

    data class Tutee(var uid: String,
                     var acctType: String,
                     var fcm_id: String,
                     var name: String,
                     var ratingAvg: String,
                     var numOfRatings: String,
                     var requestingHelp: Boolean)

    data class HelpBroadCast(var tuteeUid: String,
                             var course: String,
                             var stillAwaitingHelp: Boolean,
                             var questionDetails: String)

    data class TutorSession(var course: Course,
                            var tutor: Tutor,
                            var tutee: Tutee,
                            var rating: Int,
                            var timeElapsed: Long)

    data class Subject(var name: String)

    data class Course(var name: String,
                      var fromSubjectName: String)

    data class Chat(var to: String,
                    var from: String,
                    var message: String)
}