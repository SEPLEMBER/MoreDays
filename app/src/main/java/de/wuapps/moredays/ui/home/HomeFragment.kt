package de.wuapps.moredays.ui.home

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.R
import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    @InternalCoroutinesApi
    lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private val adapter = EntryTypeAdapter()
    private var date = Calendar.getInstance()
    private var goalsData: List<GoalAndRelatedData>? = null
    private var scales: MutableList<Scale> = mutableListOf()

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        application = requireNotNull(this.activity).application
        db = MoreDaysDatabase.getInstance(application)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                db.goalDao,
                db.scaleDao,
                db.challengeDao,
                db.activityEntryDao,
                db.scaleEntryDao
            )
        ).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewEntryTypes.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.goal_item_width).toInt()
        )
        binding.recyclerViewEntryTypes.adapter = adapter
        subscribeUi()
        return binding.root
    }

    fun subscribeUi() {
        homeViewModel.entryTypes.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<EntryType>)
            }
        })
    }
}