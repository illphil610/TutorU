package com.newwesterndev.tutoru.utilities

import android.support.design.widget.Snackbar
import android.view.View

class Utility {

    fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }
}