package de.wuapps.moredays

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log


//see https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed(({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }), 4000)

    }
}

