package de.wuapps.moredays.ui.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import de.wuapps.moredays.R

class Onboarding : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            AppIntroFragment.newInstance(

                backgroundColor = getColor(R.color.orange500),
                titleColor = getColor(R.color.gray900),
                descriptionColor = getColor(R.color.gray900),
                title = getString(R.string.onboarding_title1),
                description = getString(R.string.onboarding_desc1),
                imageDrawable = R.drawable.ic_privacy
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                backgroundColor = getColor(R.color.lime500),
                titleColor = getColor(R.color.gray600),
                descriptionColor = getColor(R.color.gray600),
                title = getString(R.string.onboarding_title2),
                description = getString(R.string.onboarding_desc2),
                imageDrawable = R.drawable.ic_intro_2
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                backgroundColor = getColor(R.color.orange100),
                titleColor = getColor(R.color.gray900),
                descriptionColor = getColor(R.color.gray900),
                title = getString(R.string.onboarding_title3),
                description = getString(R.string.onboarding_desc3),
                imageDrawable = R.drawable.ic_intro_3
            )
        )
        this.setColorDoneText(getColor(R.color.gray900))
        this.setIndicatorColor(getColor(R.color.gray900), getColor(R.color.white))
        this.setNextArrowColor(getColor(R.color.gray900))
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preferences_file_key),
            MODE_PRIVATE
        )
        sharedPref.edit().putBoolean(getString(R.string.preferences_onboarding), true).apply()
        finish()
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }


}
