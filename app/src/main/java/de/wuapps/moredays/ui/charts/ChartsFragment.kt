package de.wuapps.moredays.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.ChartsFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab

class ChartsFragment : Fragment() {
    private lateinit var binding: ChartsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChartsFragmentBinding.inflate(inflater, container, false)
        wireUpViewPagerAndTabs()
        handleFab(false)
        return binding.root
    }

    private fun wireUpViewPagerAndTabs() {
        var adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(GoalsChartsFragment(), getString(R.string.goals))
        adapter.addFragment(ScalesChartFragment(), getString(R.string.scales))

        binding.viewPager.setAdapter(adapter)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

}