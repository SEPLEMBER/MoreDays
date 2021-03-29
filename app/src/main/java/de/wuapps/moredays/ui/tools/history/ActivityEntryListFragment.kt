package de.wuapps.moredays.ui.tools.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.database.entity.ActivityEntryAndRelatedData
import de.wuapps.moredays.databinding.ActivityEntryListFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi


class ActivityEntryListFragment : Fragment() {

    private var _binding: ActivityEntryListFragmentBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ActivityEntryAdapter? = null
    private val adapter get() = _adapter!!
    private val viewModel: ActivityEntryListViewModel by viewModels {
        InjectorUtils.provideActivityEntryListViewModelFactory(requireActivity())
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityEntryListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewActivityEntries.layoutManager = layoutManager
        _adapter = ActivityEntryAdapter()
        binding.recyclerviewActivityEntries.adapter = adapter
        val touchHelper = getItemTouchHelper()
        touchHelper.attachToRecyclerView(binding.recyclerviewActivityEntries)
        subscribeUi()
        handleFab(viewModel.hasFab)
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi() {
        viewModel.activityEntries.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<ActivityEntryAndRelatedData>)
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
                val id = adapter.getActivityEntryId(viewHolder.adapterPosition)
                viewModel.removeActivityEntry(id)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}