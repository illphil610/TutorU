package com.newwesterndev.tutoru.activities.Tutor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.newwesterndev.tutoru.R
import com.newwesterndev.tutoru.activities.Tutee.HelpRequestActivity
import com.newwesterndev.tutoru.utilities.FirebaseManager
import kotlinx.android.synthetic.main.activity_ratings.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class RatingsActivity : AppCompatActivity() {

    private lateinit var mFirebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratings)

        mFirebaseManager = FirebaseManager.instance
        val mAuth = FirebaseAuth.getInstance()

        // figure out if the user is a Tutor or a Tutee
        val userUid = intent.getStringExtra("uid")
        Log.e("uid", userUid)
        val preferences = getSharedPreferences(getString(R.string.sharedPrefs), Context.MODE_PRIVATE)
        userType = preferences.getString(mAuth.currentUser?.email, getString(R.string.unknown))
        Log.e("UserType", userType)

        submit_rating_button.setOnClickListener {

            when (userType) {
                getString(R.string.tuteeMain) -> {
                    // since we are rating the Tutor
                    mFirebaseManager.getTutor(userUid) { tutor ->
                        // get the old stuff and create the new stuff yay
                        val currentRating = tutor.ratingAvg.toDouble()

                        Log.e("rating as doubl", currentRating.toString())
                        val currentRatingCount = tutor.numOfRatings.toInt()
                        val newRatingAverage = ( (currentRating + ratingBar2.rating) / (currentRatingCount + 1))
                        Log.e("new avg", newRatingAverage.toString())
                        // set the new stuff even more yayy
                        tutor.ratingAvg = newRatingAverage.toString()
                        tutor.numOfRatings = (currentRatingCount + 1).toString()
                        mFirebaseManager.updateTutor(userUid, tutor)
                    }
                }
                getString(R.string.tutorMain) -> {
                    mFirebaseManager.getTutee(userUid) { tutee ->
                        // get the old stuff and create the new stuff yay
                        val currentRating = tutee.ratingAvg.toDouble()

                        Log.e("rating as doubl", currentRating.toString())
                        val currentRatingCount = tutee.numOfRatings.toInt()
                        val newRatingAverage = ( (currentRating + ratingBar2.rating) / (currentRatingCount + 1))
                        Log.e("new avg", newRatingAverage.toString())
                        // set the new stuff even more yayy
                        tutee.ratingAvg = newRatingAverage.toString()
                        tutee.numOfRatings = (currentRatingCount + 1).toString()
                        mFirebaseManager.updateTutee(userUid, tutee)
                    }
                }
                else -> {

                }
            }

            if (ratingBar2.rating == 5.0F) {
                // This was Lee's idea and its really cool!
                viewKonfetti.build()
                        .addColors(Color.parseColor("#00ADEF"), Color.parseColor("#FF4081"), Color.parseColor("#007ebc"))
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(Size(12))
                        .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                        .stream(300, 5000L)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //check if user is a Tutor or a tutee
        if (userType == "tutee") {
            val intent = Intent(this, HelpRequestActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            val intent = Intent(this, TutorProfileActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    companion object {
        private var userType = String()
    }
}
