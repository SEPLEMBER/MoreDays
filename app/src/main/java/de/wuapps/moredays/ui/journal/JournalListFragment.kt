package de.wuapps.moredays.ui.journal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.JournalListFragmentBinding
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import kotlinx.coroutines.InternalCoroutinesApi

class JournalListFragment : Fragment() {

    private lateinit var viewModel: JournalListViewModel
    private lateinit var binding: JournalListFragmentBinding

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val db = MoreDaysDatabase.getInstance(application)
        viewModel = ViewModelProvider(
            this,
            JournalListViewModelFactory(
                db.journalDao
            )
        ).get(JournalListViewModel::class.java)

        binding = JournalListFragmentBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(this.activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
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