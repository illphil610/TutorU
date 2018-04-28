package com.newwesterndev.tutoru.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.newwesterndev.tutoru.R
import io.reactivex.disposables.CompositeDisposable

class MessageActivity : AppCompatActivity() {

    private var mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
    }
}
