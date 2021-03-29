package de.wuapps.moredays.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalListFragmentBinding
import de.wuapps.moredays.ui.tools.getFlexboxLayoutManager
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi


class GoalListFragment : Fragment() {

    private lateinit var binding: GoalListFragmentBinding

    private val viewModel: GoalListViewModel by viewModels {
        InjectorUtils.provideGoalListViewModelFactory(requireActivity())
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GoalListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewGoals.layoutManager = getFlexboxLayoutManager(requireContext())
        val adapter = GoalAdapter()
        binding.recyclerviewGoals.adapter = adapter
        subscribeUi(adapter)
        setFabOnClickListener{findNavController().navigate(R.id.navigation_goals_goal)}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: GoalAdapter) {
        viewModel.goals.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Goal>)
            }
        })
    }
}