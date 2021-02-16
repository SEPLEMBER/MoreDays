package de.wuapps.moredays.ui.tools.trophy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.databinding.TrophyListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.journal.JournalListViewModel
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi

class TrophyListFragment : Fragment() {

    private lateinit var binding: TrophyListFragmentBinding

    private val viewModel: TrophyListViewModel by viewModels {
        InjectorUtils.provideTrophyListViewModelFactory(requireActivity())
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TrophyListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.goal_item_width).toInt()
        )
        binding.recyclerviewTrophies.layoutManager = layoutManager
        val adapter = TrophyAdapter()
        binding.recyclerviewTrophies.adapter = adapter
        subscribeUi(adapter)
        handleFab(viewModel.hasFab)
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: TrophyAdapter) {
        viewModel.trophies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<TrophyAndRelatedGoal>)
            }
        })
    }
}