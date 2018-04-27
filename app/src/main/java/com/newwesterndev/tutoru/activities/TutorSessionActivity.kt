package com.newwesterndev.tutoru.activities

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.utilities.FirebaseManager

import kotlinx.android.synthetic.main.activity_tutor_session.*
import android.R.attr.tag
import android.nfc.tech.Ndef



class TutorSessionActivity : AppCompatActivity() {

    internal var mPendingIntent: PendingIntent?=null
    internal val fireBaseManager : FirebaseManager = FirebaseManager.instance

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
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            writePayload(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        }

    }

    private fun writePayload(tag: Tag) {
       val tutorRecord : NdefRecord = NdefRecord.createTextRecord(null, "Testing ID")
        val appRecord : NdefRecord = NdefRecord.createApplicationRecord(packageName)
        val message = NdefMessage(arrayOf(tutorRecord, appRecord))

        try {
            val ndef = Ndef.get(tag)
            ndef.connect()
            ndef.writeNdefMessage(message)
        } catch ( e : Exception){
            e.printStackTrace()
        }
    }
}


