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
import de.wuapps.moredays.databinding.FragmentJournalListBinding
import de.wuapps.moredays.ui.tools.getFlexboxLayoutManager
import de.wuapps.moredays.ui.tools.handleFab
import de.wuapps.moredays.ui.tools.setFabOnClickListener
import de.wuapps.moredays.ui.tools.showFab
import de.wuapps.moredays.utilities.InjectorUtils
import kotlinx.coroutines.InternalCoroutinesApi

class JournalListFragment : Fragment() {

    private val viewModel: JournalListViewModel by viewModels {
        InjectorUtils.provideJournalListViewModelFactory(requireActivity())
    }
    private var _binding: FragmentJournalListBinding? = null
    private val binding get() = _binding!!
    private var _adapter: JournalAdapter? = null
    private val adapter get() = _adapter!!
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJournalListBinding.inflate(inflater, container, false)
        binding.recyclerviewJournal.layoutManager = getFlexboxLayoutManager(requireContext())
        _adapter = JournalAdapter()
        binding.recyclerviewJournal.adapter = adapter
        subscribeUi()
        setFabOnClickListener{
            findNavController().navigate(R.id.navigation_journalList_journal)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showFab()
    }

    private fun subscribeUi() {
        viewModel.journalList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<Journal>)
                binding.textViewToday.text = it.firstOrNull()?.affirmation ?: getString(R.string.label_journal_list_today)
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }
}