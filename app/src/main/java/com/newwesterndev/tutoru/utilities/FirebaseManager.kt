package com.newwesterndev.tutoru.utilities

import android.location.Location
import android.util.Log
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
    private val mTutorDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Contract.TUTOR)
    private var mTuteeDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Contract.TUTEE)

    fun logIntoFirebase(email: String, password: String, callback: (String) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            callback(it.toString())
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

    fun getTutor(uid: String, callback: (Model.Tutor) -> Unit) {
        mTutorDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                Log.e("Tutor", snapshot.toString())
                if (snapshot != null) {
                    Log.e("Tutor Deets", snapshot.child("name").value as String)
                    callback(Model.Tutor(uid, snapshot.child("name").value as String, true))
                }
            }
            override fun onCancelled(error: DatabaseError?) {
                Log.e("fireDbError", error.toString())
            }
        })
    }

    fun getTutee(uid: String, callback: (Model.Tutee) -> Unit) {
        mTuteeDbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                Log.e("Tutee", snapshot.toString())
                if (snapshot != null) {
                    Log.e("Tutee Deets", snapshot.child("name").value as String)
                    callback(Model.Tutee(uid, snapshot.child("name").value as String, true))
                }
            }
            override fun onCancelled(error: DatabaseError?) {
                Log.e("fireDbError", error.toString())
            }
        })
    }

    // save the Tutees location to the Geofire table
    fun saveGeoFireDataForTutee(uid: String, location: Location) {}

    companion object {
        val instance: FirebaseManager by lazy { Holder.INSTANCE }
    }
}