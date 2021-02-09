package de.wuapps.moredays.ui.tools.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.data.entity.ActivityEntryAndRelatedData
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.databinding.ActivityEntryListFragmentBinding
import kotlinx.coroutines.InternalCoroutinesApi


class ActivityEntryListFragment : Fragment() {

    private lateinit var binding: ActivityEntryListFragmentBinding
    private lateinit var adapter: ActivityEntryAdapter
    @InternalCoroutinesApi
    private lateinit var viewModel: ActivityEntryListViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        viewModel = ViewModelProvider(
            this,
            ActivityEntryListViewModelFactory(
                db.activityEntryDao
            )
        ).get(ActivityEntryListViewModel::class.java)

        binding = ActivityEntryListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(application)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerviewActivityEntries.layoutManager = layoutManager
        adapter = ActivityEntryAdapter()
        binding.recyclerviewActivityEntries.adapter = adapter
        val touchHelper = getItemTouchHelper()
        touchHelper.attachToRecyclerView(binding.recyclerviewActivityEntries)
        subscribeUi(adapter)
        return binding.root
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: ActivityEntryAdapter) {
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
}