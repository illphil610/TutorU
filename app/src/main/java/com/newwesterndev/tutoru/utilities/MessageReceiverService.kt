package com.newwesterndev.tutoru.utilities

import android.util.Log
import co.intentservice.chatui.models.ChatMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.newwesterndev.tutoru.model.Model
import com.newwesterndev.tutoru.utilities.RxBus
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.newwesterndev.tutoru.R


class MessageReceiverService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        Log.e("Rec'v msg", message?.notification?.body.toString())

        val message = Model.Chat("me", "you", message?.notification?.body.toString())
        RxBus.publish(message)


        val intent = Intent(this, ChatMessage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val mBuilder = NotificationCompat.Builder(this, "channelId")
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("New Message!")
                .setContentText("TutorU needs your help!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
    }
}