package com.newwesterndev.tutoru.utilities

import android.content.Context
import android.nfc.NdefRecord
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.newwesterndev.tutoru.model.Contract

class Utility {

    fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    fun createNdefRecord(uid: String): Array<NdefRecord> {
        val keysRecord = NdefRecord.createMime("text/plain", uid.toByteArray())
        return arrayOf(keysRecord)
    }

    fun isValidEmail(email: CharSequence?): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        var digit = false
        var upperCaseLetter = false
        var lowerCaseLetter = false

        if (password != null && password.length > 6 && password.length < 18) {
            var index = 0
            while(index < password.length) {
                val character = password[index]
                when {
                    Character.isWhitespace(character) -> return false
                    Character.isLowerCase(character) -> lowerCaseLetter = true
                    Character.isUpperCase(character) -> upperCaseLetter = true
                    Character.isDigit(character) -> digit = true
                }
                index++
            }
        }
        return digit && upperCaseLetter && lowerCaseLetter
    }
}