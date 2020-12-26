package de.wuapps.moredays.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.databinding.ActivityListFragmentBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager

class ActivityListFragment : Fragment() {

    lateinit var binding: ActivityListFragmentBinding

    private lateinit var viewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        viewModel = ActivityViewModel( ActivityRepository(MoreDaysDatabase.getInstance(application).activityDao),  GoalRepository(MoreDaysDatabase.getInstance(application).goalDao), -1)
        binding = ActivityListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewActivities.layoutManager = ResponsiveGridLayoutManager(requireNotNull(this.activity), resources.getDimension(R.dimen.activity_item_width).toInt())
        val adapter = ActivityAdapter()
        binding.recyclerviewActivities.adapter = adapter
        subscribeUi(adapter, binding)

        //if (viewModel.isPossible!!)
            binding.fabAdd.setOnClickListener({findNavController().navigate(R.id.navigation_activities_activity)})
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    private fun subscribeUi(adapter: ActivityAdapter, binding: ActivityListFragmentBinding) {
        viewModel.activities.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Activity>)

            }
        })
//        viewModel.activitys.observe(viewLifecycleOwner) { result ->
//            adapter.notifyDataSetChanged()
//        }
    }
}