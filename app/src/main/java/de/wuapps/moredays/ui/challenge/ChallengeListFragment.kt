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
import de.wuapps.moredays.databinding.FragmentChallengeListBinding
import de.wuapps.moredays.ui.tools.getFlexboxLayoutManager
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils

class ChallengeListFragment : Fragment() {

    private var _binding: FragmentChallengeListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ChallengeAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: ChallengeListViewModel by viewModels {
        InjectorUtils.provideChallengeListViewModelFactory(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeListBinding.inflate(inflater, container, false)
        binding.recyclerviewChallenges.layoutManager = getFlexboxLayoutManager(requireContext())
        _adapter = ChallengeAdapter()
        binding.recyclerviewChallenges.adapter = adapter
        subscribeUi()
        setFabOnClickListener{findNavController().navigate(R.id.navgigation_challenges_challenge)}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi() {
        viewModel.challenges.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Challenge>)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        setFabOnClickListener(null)
        _binding = null
    }

}