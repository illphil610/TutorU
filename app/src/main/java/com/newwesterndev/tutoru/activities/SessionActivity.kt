package com.newwesterndev.tutoru.activities

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.utilities.Utility

class SessionActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mUtility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutee_session)

        // NFC stuff and things
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!mNfcAdapter.isEnabled) {
            mUtility.showMessage(View(this), "Please enable NFC in settings")
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this)
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            handleIntent(intent)
        }
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        //Log.e("ONDEFPUSHCOMPLETE", "FUCK YEAHHH")
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        // just a place holder
        return NdefMessage(NdefRecord.createApplicationRecord("poop"))

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent?) {
        val rawMessages = intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        val message = rawMessages?.get(0) as NdefMessage
        val mReceivedUserName = message.records[0].payload
        Log.e("Received User Name:", String(mReceivedUserName))
    }
}
