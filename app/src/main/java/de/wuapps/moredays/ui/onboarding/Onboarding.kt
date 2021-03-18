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

                backgroundColor = getColor(R.color.primaryColor),
                titleColor = getColor(R.color.primaryTextColor),
                descriptionColor = getColor(R.color.primaryTextColor),
                title = getString(R.string.onboarding_title1),
                description = getString(R.string.onboarding_desc1),
                imageDrawable = R.drawable.ic_privacy
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                backgroundColor = getColor(R.color.secondaryColor),
                titleColor = getColor(R.color.secondaryTextColor),
                descriptionColor = getColor(R.color.secondaryTextColor),
                title = getString(R.string.onboarding_title2),
                description = getString(R.string.onboarding_desc2),
                imageDrawable = R.drawable.ic_intro_2
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                backgroundColor = getColor(R.color.primaryVariantColor),
                titleColor = getColor(R.color.primaryTextColor),
                descriptionColor = getColor(R.color.primaryTextColor),
                title = getString(R.string.onboarding_title3),
                description = getString(R.string.onboarding_desc3),
                imageDrawable = R.drawable.ic_intro_3
            )
        )
        this.setColorDoneText(getColor(R.color.primaryTextColor))
        this.setIndicatorColor(getColor(R.color.primaryTextColor), getColor(R.color.white))
        this.setNextArrowColor(getColor(R.color.primaryTextColor))
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preferences_file_key),
            MODE_PRIVATE
        )
        val editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.preferences_onboarding), true)
        editor.apply()
        finish()
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }


}
