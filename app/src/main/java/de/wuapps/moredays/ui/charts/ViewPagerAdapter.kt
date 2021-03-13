package de.wuapps.moredays.ui.charts


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager


class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private final var tabFragments: ArrayList<Pair<Fragment, String>> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return tabFragments.get(position).first
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence {
        return tabFragments.get(position).second
    }

    override fun getCount(): Int {
        return tabFragments.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        tabFragments.add(Pair(fragment, title))
    }
}