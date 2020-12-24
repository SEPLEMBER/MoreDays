package de.wuapps.moredays.ui.goal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.dao.GoalDao
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalFragmentBinding
import de.wuapps.moredays.databinding.GoalListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager

class GoalListFragment : Fragment() {

    lateinit var binding: GoalListFragmentBinding

    private lateinit var viewModel: GoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val goalRepository = GoalRepository(MoreDaysDatabase.getInstance(application).goalDao)
        viewModel = GoalViewModel(goalRepository, -1, application)
        binding = GoalListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewGoals.layoutManager = ResponsiveGridLayoutManager(requireNotNull(this.activity), resources.getDimension(R.dimen.goal_item_width).toInt())
        val adapter = GoalAdapter()
        binding.recyclerviewGoals.adapter = adapter
        subscribeUi(adapter, binding)

        binding.fabAdd.setOnClickListener({findNavController().navigate(R.id.navigation_goals_goal)})
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    private fun subscribeUi(adapter: GoalAdapter, binding: GoalListFragmentBinding) {
        viewModel.goals.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Goal>)

            }
        })
//        viewModel.goals.observe(viewLifecycleOwner) { result ->
//            adapter.notifyDataSetChanged()
//        }
    }
}