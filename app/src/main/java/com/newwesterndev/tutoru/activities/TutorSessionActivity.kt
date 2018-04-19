package com.newwesterndev.tutoru.activities

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.newwesterndev.tutoru.R

import kotlinx.android.synthetic.main.activity_tutor_session.*

class TutorSessionActivity : AppCompatActivity() {

    internal var mPendingIntent: PendingIntent?=null
    //FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_session)

        val intent = Intent(this@TutorSessionActivity, TutorSessionActivity::class.java)
        mPendingIntent = PendingIntent.getActivity(this@TutorSessionActivity, 0, intent, 0)

    }

    override fun onResume() {
        super.onResume()
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //Send payload containing TutorID to Tutee, via NFC

    }

    private fun writePayload(tag: Tag) {
        //Write TutorID to payload, sent through via NFC to Tutee, initiating TutorSession afterwards

    }
}


