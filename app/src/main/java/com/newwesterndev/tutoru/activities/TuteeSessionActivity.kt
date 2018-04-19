package com.newwesterndev.tutoru.activities

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.newwesterndev.tutoru.R

class TuteeSessionActivity : AppCompatActivity() {

    internal var mPendingIntent: PendingIntent?= null
    //FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java_tutor_session)

        val intent = Intent(this@TuteeSessionActivity, TuteeSessionActivity::class.java)
        mPendingIntent = PendingIntent.getActivity(this@TuteeSessionActivity, 0, intent, 0)

        (findViewById(R.id.endSessionButton) as Button)
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
        //Read payload containing TutorID from Tutor, via NFC

    }

    private fun readPayload(intent: Intent) {
        //Read TutorId, sent through via NFC from Tutor, initiating TutorSession afterwards and displaying
        // to the Tutee
        val payload = String((intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0] as NdefMessage)
                .records[0].payload)
        (findViewById(R.id.tuteeText) as TextView).text = payload
    }
}

