package de.wuapps.moredays

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.wuapps.moredays.ui.onboarding.Onboarding
import java.util.*


//see https://www.bignerdranch.com/blog/splash-screens-the-right-way/

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        if (sharedPref.getBoolean(getString(R.string.preferences_onboarding), false)) {
            val txtSaying = findViewById<TextView>(R.id.saying)
            val sayings = resources.getStringArray(R.array.sayings)
            val random = Calendar.getInstance().timeInMillis % sayings.size
            txtSaying.text = sayings[random.toInt()]
            val preferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
            val txtGreeting = findViewById<TextView>(R.id.greeting)
            txtGreeting.text =
                "Hi " + sharedPref.getString(getString(R.string.preferences_name_key), "")
            Handler(Looper.getMainLooper()).postDelayed(({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }), 4000)
        }
        else {
            val intentOnboarding = Intent(this, Onboarding::class.java)
            startActivity(intentOnboarding)
            finish()
        }
    }
}

