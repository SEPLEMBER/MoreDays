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
import de.wuapps.moredays.databinding.FragmentGoalListBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.getFlexboxLayoutManager
import de.wuapps.moredays.utilities.setFabOnClickListener
import de.wuapps.moredays.utilities.showFab
import kotlinx.coroutines.InternalCoroutinesApi


class GoalListFragment : Fragment() {

    private var _binding: FragmentGoalListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: GoalAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: GoalListViewModel by viewModels {
        InjectorUtils.provideGoalListViewModelFactory(requireActivity())
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalListBinding.inflate(inflater, container, false)
        binding.recyclerviewGoals.layoutManager = getFlexboxLayoutManager(requireContext())
        _adapter = GoalAdapter()
        binding.recyclerviewGoals.adapter = adapter
        subscribeUi()
        setFabOnClickListener{findNavController().navigate(R.id.navigation_goals_goal)}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi() {
        viewModel.goals.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it as MutableList<Goal>)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}