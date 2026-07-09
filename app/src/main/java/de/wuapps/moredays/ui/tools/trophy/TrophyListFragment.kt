package de.wuapps.moredays.ui.tools.trophy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.databinding.FragmentTrophyListBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import kotlinx.coroutines.InternalCoroutinesApi

class TrophyListFragment : Fragment() {

    private var _binding: FragmentTrophyListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: TrophyAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: TrophyListViewModel by viewModels {
        InjectorUtils.provideTrophyListViewModelFactory(requireActivity())
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrophyListBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewTrophies.layoutManager = layoutManager
        _adapter = TrophyAdapter()
        binding.recyclerviewTrophies.adapter = adapter
        subscribeUi()
        handleFab(viewModel.hasFab)
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi() {
        viewModel.trophies.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it as MutableList<TrophyAndRelatedGoal>)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}
