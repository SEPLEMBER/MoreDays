package de.wuapps.moredays.ui.tools.trophy

import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.wuapps.moredays.R
import de.wuapps.moredays.data.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.GoalListFragmentBinding
import de.wuapps.moredays.databinding.TrophyListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.goal.GoalAdapter
import de.wuapps.moredays.ui.goal.GoalListViewModel
import de.wuapps.moredays.ui.goal.GoalListViewModelFactory
import kotlinx.coroutines.InternalCoroutinesApi

class TrophyListFragment : Fragment() {

    private lateinit var binding: TrophyListFragmentBinding

    @InternalCoroutinesApi
    private lateinit var viewModel: TrophyListViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        viewModel = ViewModelProvider(
            this,
            TrophyListViewModelFactory(
                db.trophyDao
            )
        ).get(TrophyListViewModel::class.java)

        binding = TrophyListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.goal_item_width).toInt()
        )
        binding.recyclerviewTrophies.layoutManager = layoutManager
        val adapter = TrophyAdapter()
        binding.recyclerviewTrophies.adapter = adapter
        subscribeUi(adapter)
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