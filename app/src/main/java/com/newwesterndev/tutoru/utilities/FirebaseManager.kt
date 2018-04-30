package com.newwesterndev.tutoru.utilities

import android.content.Context
import android.location.Location
import android.util.Log
import co.intentservice.chatui.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model

class FirebaseManager private constructor() {

    // Singleton instance
    private object Holder { val INSTANCE = FirebaseManager() }

    private var mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mDatabaseReference: DatabaseReference = mFirebaseDatabase.reference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mUserTypeRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("UserType")
    private val mTutorDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Contract.TUTOR)
    private var mTuteeDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Contract.TUTEE)
    private var mMessagesDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Messages")

    fun logIntoFirebase(email: String, password: String, callback: (String) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            callback(it.toString())
        }
    }

    fun getUserUniqueId(): String {
        return if (mAuth.currentUser?.uid != null) {
            mAuth.currentUser!!.uid
        } else {
            Log.e("TAG", "sorry fam, no UID").toString()
        }
    }

    fun sendHelpBroadcastRequest(helpBroadCast: Model.HelpBroadCast) {
        val newHelpBroadcast = mDatabaseReference.child(Contract.HELP_BROADCAST).push()
        newHelpBroadcast.setValue(helpBroadCast)
    }

    fun createTutee(tutee: Model.Tutee) {
        val newTutee = mDatabaseReference.child(Contract.TUTEE)
        newTutee.child(mAuth.currentUser?.uid).setValue(tutee)
    }

    fun createTutor(tutor: Model.Tutor) {
        val newTutor = mDatabaseReference.child(Contract.TUTOR)
        newTutor.child(mAuth.currentUser?.uid).setValue(tutor)
    }

    fun updateTutor(uid: String, tutor: Model.Tutor) {
        val updatedTutorRef = mDatabaseReference.child(Contract.TUTOR)
        updatedTutorRef.child(uid).setValue(tutor)
    }

    fun updateTutorsCourseList(context: Context, uid: String, updatedCourses: ArrayList<String>) {
        val updatedTutorRef = mDatabaseReference.child(Contract.TUTOR)
        updatedTutorRef.child(uid).child("courseList").setValue(updatedCourses)
    }

    fun updateTutee(uid: String, tutee: Model.Tutee) {
        val updatedTuteeRef = mDatabaseReference.child(Contract.TUTEE)
        updatedTuteeRef.child(uid).setValue(tutee)
    }

    fun getTutor(uid: String, callback: (Model.Tutor) -> Unit) {
        mTutorDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                if (snapshot != null) {
                             try {
                                 val tutor = Model.Tutor(uid,
                                         snapshot.child("acctType").value as String,
                                         snapshot.child("fcm_id").value as String,
                                         snapshot.child("name").value as String,
                                         snapshot.child("ratingAvg").value as String,
                                         snapshot.child("numOfRatings").value as String, true,
                                         snapshot.child("courseList").value as ArrayList<String>)
                                 callback(tutor)
                             } catch (e: Exception) {
                                 Log.e("error", "something happened right here bruh")
                             }
                }
            }
            override fun onCancelled(error: DatabaseError?) {
                Log.e("fireDbError", error.toString())
            }
        })
    }

    fun getUsersType(uid: String, callback: (String) -> Unit) {
        mUserTypeRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot != null) {
                    try {
                        Log.e("UserTupe", snapshot.child("acctType").value as String)
                        callback(snapshot.child("acctType").value.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError?) {}
        })
    }

    fun saveUsersType(uid: String, acctType: String) {
        val newTutor = mDatabaseReference.child("UserType")
        newTutor.child(uid).setValue(acctType)
    }

    fun getTutee(uid: String, callback: (Model.Tutee) -> Unit) {
        mTuteeDbRef.child(uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                if (snapshot != null) {
                    try {
                        callback(Model.Tutee(uid,
                                snapshot.child("acctType").value as String,
                                snapshot.child("fcm_id")?.value as String,
                                snapshot.child("name")?.value as String,
                                snapshot.child("ratingAvg")?.value as String,
                                snapshot.child("numOfRatings")?.value as String, true))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        Log.e("fucked", "get fucked")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError?) {
                Log.e("fireDbError", error.toString())
            }
        })
    }

    fun saveChatToFirebaseMessage(chatMessage: Model.Chat) {
        val key = mMessagesDbRef.push().key
        mMessagesDbRef.child(key).setValue(chatMessage)
    }

    companion object {
        val instance: FirebaseManager by lazy { Holder.INSTANCE }
    }
}