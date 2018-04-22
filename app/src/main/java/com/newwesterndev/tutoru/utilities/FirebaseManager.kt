package com.newwesterndev.tutoru.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.newwesterndev.tutoru.model.Contract
import com.newwesterndev.tutoru.model.Model

class FirebaseManager private constructor() {

    private object Holder { val INSTANCE = FirebaseManager() }

    private var mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mDatabaseReference: DatabaseReference = mFirebaseDatabase.reference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mStorageReference = FirebaseStorage.getInstance().reference

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

    companion object {
        val instance: FirebaseManager by lazy { Holder.INSTANCE }
    }
}