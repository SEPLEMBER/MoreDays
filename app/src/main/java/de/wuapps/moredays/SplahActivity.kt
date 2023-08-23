package de.wuapps.moredays

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.wuapps.moredays.ui.onboarding.Onboarding
import java.util.*


//see https://www.bignerdranch.com/blog/splash-screens-the-right-way/

class SplashActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        if (sharedPref.getBoolean(getString(R.string.preferences_onboarding), false)) {
            val txtSaying = findViewById<TextView>(R.id.saying)
            val txtGreeting = findViewById<TextView>(R.id.greeting)
            txtGreeting.text =
                "Hi " + sharedPref.getString(getString(R.string.preferences_name_key), "")
            if (sharedPref.getBoolean(getString(R.string.preferences_hide_affirmation_key), false)) {
                txtSaying.text = ""
                startHomeScreen()
            }
            else {
                val sayings = resources.getStringArray(R.array.sayings)
                val random = Calendar.getInstance().timeInMillis % sayings.size
                txtSaying.text = sayings[random.toInt()]

                findViewById<Button>(R.id.btnSkip).setOnClickListener { stopSplash() }
                handler = Handler(Looper.getMainLooper())
                runnable = Runnable { startHomeScreen() }
                handler.postDelayed(runnable, 4000)
            }
        }
        else {
            val intentOnboarding = Intent(this, Onboarding::class.java)
            startActivity(intentOnboarding)
            finish()
        }
    }

    private fun stopSplash(){
        handler.removeCallbacks(runnable)
        startHomeScreen()
    }

    private fun startHomeScreen() {
        Log.d("splash", "start main activity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

