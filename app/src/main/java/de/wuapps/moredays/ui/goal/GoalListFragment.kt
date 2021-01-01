package de.wuapps.moredays.ui.goal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi


class GoalListFragment : Fragment() {

    lateinit var binding: GoalListFragmentBinding

    @InternalCoroutinesApi
    private lateinit var viewModel: GoalListViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.w("home", "onCreateView GoalListFragment")
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        viewModel = ViewModelProvider(
            this,
            GoalListViewModelFactory(
                db.goalDao
            )
        ).get(GoalListViewModel::class.java)

        binding = GoalListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewGoals.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.goal_item_width).toInt()
        )
        val adapter = GoalAdapter()
        binding.recyclerviewGoals.adapter = adapter
        subscribeUi(adapter)

        binding.fabAdd.setOnClickListener({ findNavController().navigate(R.id.navigation_goals_goal) })
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: GoalAdapter) {
        Log.w("home", "subscribeUi GoalListFragment")
        viewModel.goals.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Goal>)
                Log.w("home", "subscribeUi: set adapterlist GoalListFragment")
            }
        })
    }
}