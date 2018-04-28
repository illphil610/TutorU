package com.newwesterndev.tutoru.utilities

import android.util.Log
import co.intentservice.chatui.models.ChatMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.RxBus

class MessageReceiverService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        Log.e("Rec'v msg", message?.notification?.body.toString())

        val message = Model.Chat("me", "you", message?.notification?.body.toString())
        RxBus.publish(message)
    }
}