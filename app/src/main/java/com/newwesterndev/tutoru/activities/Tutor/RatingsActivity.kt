package com.newwesterndev.tutoru.activities.Tutor

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.newwesterndev.tutoru.R
import kotlinx.android.synthetic.main.activity_ratings.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class RatingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratings)

        submit_rating_button.setOnClickListener {
            if (ratingBar2.rating == 5.0F) {
                // This was Lee's idea and its really cool
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
}
