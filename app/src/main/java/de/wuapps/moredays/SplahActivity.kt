package de.wuapps.moredays

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


//see https://www.bignerdranch.com/blog/splash-screens-the-right-way/
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

