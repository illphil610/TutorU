package com.newwesterndev.tutoru.activities

import android.app.AlertDialog
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Auth.TuteeRegisterActivity
import com.newwesterndev.tutoru.activities.Auth.TutorRegisterActivity
import com.newwesterndev.tutoru.activities.Tutor.RatingsActivity
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.Utility
import kotlinx.android.synthetic.main.activity_session.*

class SessionActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mFirebaseManager: FirebaseManager
    private lateinit var mUtility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        // firebase and utility stuff and things...
        mUtility = Utility()
        mFirebaseManager = FirebaseManager.instance

        // NFC stuff and things
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!mNfcAdapter.isEnabled) {
            mUtility.showMessage(View(this), "Please enable NFC in settings")
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this)
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this)

        var startTimer = 0L
        start_session_button.setOnClickListener {
            //var startTimer = 0L
            if (start_session_button.text == "Start Session") {
                startTimer { timeAtStart: Long ->
                    startTimer = timeAtStart
                }
            }
        }
        end_session_button.setOnClickListener {
            endTime(startTimer) { timeAtEnd: Double ->
                Log.e("Session Time", timeAtEnd.toString())
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            handleIntent(intent)
        }
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        Log.e("ONDEFPUSHCOMPLETE", "FUCK YEAHHH")
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val userId = mFirebaseManager.getUserUniqueId()
        val userIdRecord = NdefRecord.createMime("text/plain", userId.toByteArray())
        return NdefMessage(userIdRecord)
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

    private fun startTimer(callback: (Long) -> Unit) {
        callback(System.currentTimeMillis())
    }

    private fun endTime(startTime: Long, callback: (Double) -> Unit) {
        val tEnd = System.currentTimeMillis()
        val tDelta = tEnd - startTime
        callback(tDelta / 1000.0)
        showRatingsQuestionAlertDialog()
    }

    private fun showRatingsQuestionAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_ask_for_rating_dialog, null)
        val createdDialogView = dialogBuilder.create()
        createdDialogView.setView(dialogView)

        val yesButton = dialogView.findViewById(R.id.button_rating_yes) as Button
        val noButton = dialogView.findViewById(R.id.button_rating_no) as Button

        yesButton.setOnClickListener {
            val tutorIntent = Intent(this, RatingsActivity::class.java)
            startActivity(tutorIntent)
            createdDialogView.dismiss()
            finish()
        }

        noButton.setOnClickListener {
            createdDialogView.dismiss()
        }
        createdDialogView.show()
    }
}
