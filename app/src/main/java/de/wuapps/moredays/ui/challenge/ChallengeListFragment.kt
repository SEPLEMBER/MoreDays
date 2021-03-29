package de.wuapps.moredays.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.databinding.ChallengeListFragmentBinding
import de.wuapps.moredays.ui.tools.getFlexboxLayoutManager
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils

class ChallengeListFragment : Fragment() {

    private lateinit var binding: ChallengeListFragmentBinding

    private val viewModel: ChallengeListViewModel by viewModels {
        InjectorUtils.provideChallengeListViewModelFactory(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChallengeListFragmentBinding.inflate(inflater, container, false)
        binding.recyclerviewChallenges.layoutManager = getFlexboxLayoutManager(requireContext())
        val adapter = ChallengeAdapter()
        binding.recyclerviewChallenges.adapter = adapter
        subscribeUi(adapter)
        setFabOnClickListener{findNavController().navigate(R.id.navgigation_challenges_challenge)}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi(adapter: ChallengeAdapter) {
        viewModel.challenges.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Challenge>)
            }
        })
    }
}