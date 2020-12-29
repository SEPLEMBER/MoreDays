package de.wuapps.moredays.ui.home

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.data.repository.ScaleRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.SharedViewModel
import de.wuapps.moredays.ui.SharedViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    @InternalCoroutinesApi
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private val adapter = EntryTypeAdapter()
    private var isLoading: Boolean = false

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        application = requireNotNull(this.activity).application
        db = MoreDaysDatabase.getInstance(application)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(
                ActivityRepository(db.activityDao),
                GoalRepository(db.goalDao),
                ChallengeRepository(db.challengeDao),
                ScaleRepository(db.scaleDao)
            )
        ).get(SharedViewModel::class.java)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewEntryTypes.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.goal_item_width).toInt()
        )
        binding.recyclerViewEntryTypes.adapter = adapter
        lifecycleScope.launch {
            getAllData()
        }
        return binding.root
    }

@InternalCoroutinesApi
suspend fun getAllData() {
        val listGoals: MutableList<Goal> = mutableListOf()
        val listActivities: MutableList<de.wuapps.moredays.database.entity.Activity> = mutableListOf()
        val listScales: MutableList<Scale> = mutableListOf()
        val listChallenges: MutableList<Challenge> = mutableListOf()
        lifecycleScope.launch{
            val rg = sharedViewModel._goals.firstOrNull()
            if (rg != null)
                listGoals.addAll(rg)
            val ra = sharedViewModel._activities.firstOrNull()
            if (ra != null)
                listActivities.addAll(ra)
            val rs = sharedViewModel._scales.firstOrNull()
            if (rs != null)
                listScales.addAll(rs)
            updateHomeUI(AllData(listGoals, listActivities, listScales, listChallenges))
        }
    }

    @InternalCoroutinesApi
    private fun updateHomeUI(allData: AllData) {
        val entryTypes = mutableListOf<EntryType>()
        allData.goals.forEach {
            entryTypes += GoalEntry(
                it,
                allData.activities,
                1,
                db.activityEntryDao
            )
        }
        allData.scales.forEach {
            entryTypes += ScaleEntry(
                it,
                1F,
                db.scaleEntryDao
            )
        }
        if (entryTypes.size > 0){
            adapter.submitList(entryTypes)
        }
    }

}

data class AllData(
    var goals: List<Goal>,
    val activities: List<Activity>,
    val scales: List<Scale>,
    val challenges: List<Challenge>
)