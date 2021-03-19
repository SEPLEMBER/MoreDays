package de.wuapps.moredays.ui.journal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.JournalListFragmentBinding
import de.wuapps.moredays.utilities.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi

class JournalListFragment : Fragment() {

    private val viewModel: JournalListViewModel by viewModels {
        InjectorUtils.provideJournalListViewModelFactory(requireActivity())
    }
    private lateinit var binding: JournalListFragmentBinding

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = JournalListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.journal_item_width).toInt()
        )
        binding.recyclerviewJournal.layoutManager = layoutManager
        val adapter = JournalAdapter()
        binding.recyclerviewJournal.adapter = adapter
        subscribeUi(adapter)
        setFabOnClickListener{findNavController().navigate(R.id.navigation_journalList_journal)}
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        handleFab(viewModel.hasFab)
    }

    @InternalCoroutinesApi
    private fun subscribeUi(adapter: JournalAdapter) {
        Log.w("home", "subscribeUi GoalListFragment")
        viewModel.journalList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Journal>)
            }
        })
    }

}