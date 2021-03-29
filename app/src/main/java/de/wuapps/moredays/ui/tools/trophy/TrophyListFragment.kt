package de.wuapps.moredays.ui.tools.trophy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.databinding.TrophyListFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi

class TrophyListFragment : Fragment() {

    private var _binding: TrophyListFragmentBinding? = null
    private val binding get() = _binding!!
    private var _adapter: TrophyAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: TrophyListViewModel by viewModels {
        InjectorUtils.provideTrophyListViewModelFactory(requireActivity())
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TrophyListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewTrophies.layoutManager = layoutManager
        _adapter = TrophyAdapter()
        val touchHelper = getItemTouchHelper()
        touchHelper.attachToRecyclerView(binding.recyclerviewTrophies)
        binding.recyclerviewTrophies.adapter = adapter
        subscribeUi()
        handleFab(viewModel.hasFab)
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi() {
        viewModel.trophies.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<TrophyAndRelatedGoal>)
            }
        })
    }

    @InternalCoroutinesApi
    private fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val id = adapter.getTrophyId(viewHolder.adapterPosition)
                viewModel.removeTrophy(id)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}