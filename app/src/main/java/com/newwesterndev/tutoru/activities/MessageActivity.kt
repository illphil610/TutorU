package com.newwesterndev.tutoru.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.intentservice.chatui.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.FirebaseManager
import com.newwesterndev.tutoru.utilities.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    private var mCompositeDisposable = CompositeDisposable()
    private lateinit var mFirebaseManager: FirebaseManager
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        mFirebaseManager = FirebaseManager.instance
        mAuth = FirebaseAuth.getInstance()

        val userUID = intent.getStringExtra("userKey")
        val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
        val user = preferences.getString(mAuth.currentUser?.email, getString(R.string.unknown))
        val userFcmId = preferences.getString("FCM_ID", "unknown")

        mCompositeDisposable.add(RxBus.listen(Model.Chat::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("CHATTTTT", it.message)
                    chat_view.addMessage(ChatMessage(it.message, System.currentTimeMillis(), ChatMessage.Type.RECEIVED))
                }))

        chat_view.setOnSentMessageListener {
            if (user == "tutor") {
                mFirebaseManager.getTutee(userUID) { tutee ->
                    Log.e("tutee", tutee.toString())
                    val chat = Model.Chat(tutee.fcm_id, userFcmId, it.message)
                    mFirebaseManager.saveChatToFirebaseMessage(chat)
                }
            } else {
                mFirebaseManager.getTutor(userUID) { tutor ->
                    Log.e("tutor", tutor.toString())
                    val chat = Model.Chat(tutor.fcm_id, userFcmId, it.message)
                    mFirebaseManager.saveChatToFirebaseMessage(chat)
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_signout -> {
            FirebaseAuth.getInstance().signOut()
            true
        }
        R.id.action_start_session -> {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    /*
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            val newMessage = intent?.getStringExtra("message")
            val messageFrom = intent?.getStringExtra("from")
            val messageTo = intent?.getStringExtra("to")
            RxBus.publish(Model.Chat(messageFrom!!, messageTo!!, newMessage!!))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    */

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
