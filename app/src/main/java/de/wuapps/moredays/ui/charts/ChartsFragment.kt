package de.wuapps.moredays.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.ChartsFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab


class ChartsFragment : Fragment() {
    private lateinit var binding: ChartsFragmentBinding
    private var goalsChartsFragment: GoalsChartsFragment? = null
    private var scalesChartFragment: ScalesChartFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChartsFragmentBinding.inflate(inflater, container, false)
        handleFab(false)
        wireUpTabView()
        return binding.root
    }

    private fun wireUpTabView() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.title_goals)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.title_scales)))


        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setCurrentTabFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setCurrentTabFragment(0)
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        when (tabPosition) {
            0 -> {
                if (goalsChartsFragment == null) goalsChartsFragment = GoalsChartsFragment()
                replaceFragment(goalsChartsFragment!!)
            }
            1 -> {
                if (scalesChartFragment == null) scalesChartFragment = ScalesChartFragment()
                replaceFragment(scalesChartFragment!!)
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fm: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(binding.fragmentContainer.id, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }
}