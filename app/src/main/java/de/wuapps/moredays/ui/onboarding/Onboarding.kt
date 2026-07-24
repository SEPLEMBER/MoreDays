package de.wuapps.moredays.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import android.graphics.Color


class Onboarding : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            AppIntroFragment.createInstance(

                backgroundColorRes = R.color.orange500,
                titleColorRes = R.color.gray900,
                descriptionColorRes = R.color.gray900,
                title = getString(R.string.onboarding_title1),
                description = getString(R.string.onboarding_desc1),
                imageDrawable = R.drawable.ic_privacy
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                backgroundColorRes = R.color.lime500,
                titleColorRes = R.color.gray600,
                descriptionColorRes = R.color.gray600,
                title = getString(R.string.onboarding_title2),
                description = getString(R.string.onboarding_desc2),
                imageDrawable = R.drawable.ic_intro_2
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                backgroundColorRes = R.color.orange100,
                titleColorRes = R.color.gray900,
                descriptionColorRes = R.color.gray900,
                title = getString(R.string.onboarding_title3),
                description = getString(R.string.onboarding_desc3),
                imageDrawable = R.drawable.ic_intro_3
            )
        )
        this.setColorDoneText(getColor(R.color.gray900))
        this.setIndicatorColor(getColor(R.color.gray900), getColor(R.color.white))
        this.setNextArrowColor(getColor(R.color.gray900))
		
		val skipButton = findViewById<View>(com.github.appintro.R.id.skip)
        (skipButton as? AppCompatButton)?.setTextColor(Color.parseColor("#272727"))
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startMainActivity()
    }

    private fun startMainActivity() {
        val sharedPref: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        sharedPref.edit().putBoolean(getString(R.string.preferences_onboarding), true).apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        startMainActivity()
    }



}
