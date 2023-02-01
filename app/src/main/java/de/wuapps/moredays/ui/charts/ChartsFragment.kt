package de.wuapps.moredays.ui.charts

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentChartsBinding
import de.wuapps.moredays.utilities.handleFab

class ChartsFragment : Fragment(), MenuProvider {
    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!
    private var goalsChartsFragment: GoalsChartsFragment? = null
    private var scalesChartFragment: ScalesChartFragment? = null
    private var currentTab = 0;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        handleFab(false)
        wireUpTabView()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.help_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                return true
            }
            else -> false
        }
    }

    private fun showHelp() {
        if (currentTab == 0 && goalsChartsFragment != null) {
            goalsChartsFragment!!.showHelp();
        }
        else if (currentTab == 0 && scalesChartFragment != null){

        }
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
                currentTab = 0;
                if (goalsChartsFragment == null) goalsChartsFragment = GoalsChartsFragment()
                replaceFragment(goalsChartsFragment!!)
            }
            1 -> {
                currentTab = 1;
                if (scalesChartFragment == null) scalesChartFragment = ScalesChartFragment()
                replaceFragment(scalesChartFragment!!)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fm: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(binding.fragmentContainer.id, fragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayout.clearOnTabSelectedListeners()
        goalsChartsFragment = null
        scalesChartFragment = null
        _binding = null
    }
}