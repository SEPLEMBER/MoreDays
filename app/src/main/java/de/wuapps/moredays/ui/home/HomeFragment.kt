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
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.databinding.GoalFragmentBinding
import de.wuapps.moredays.ui.AllData
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

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

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
    private fun initObservers() {
//        sharedViewModel.activities.observe(this, Observer {
//            updateHomeUI()
//        })
//        sharedViewModel.goals.observe(this, Observer {
//            updateHomeUI()
//        })
//        sharedViewModel.scales.observe(this, Observer {
//            updateHomeUI()
//        })
//        sharedViewModel.challenges.observe(this, Observer {
//            updateHomeUI()
//        })
    }

    @InternalCoroutinesApi
    suspend fun getAllData() {
        val listGoals: MutableList<Goal> = mutableListOf()
        val listActivities: MutableList<de.wuapps.moredays.database.entity.Activity> = mutableListOf()
        val listScales: MutableList<Scale> = mutableListOf()
        val listChallenges: MutableList<Challenge> = mutableListOf()
        //coroutineScope.launch {
        lifecycleScope.launch{
            Log.w("async", "fetch data in shared view model")
            //async { sharedViewModel._goals.collect { if (it != null) listGoals.addAll(it) } }
            val rg = sharedViewModel._goals.firstOrNull()
            if (rg != null)
                listGoals.addAll(rg)
            Log.w("async", "fetch others act")
            val ra = sharedViewModel._activities.firstOrNull()
            if (ra != null)
                listActivities.addAll(ra)
            val rs = sharedViewModel._scales.firstOrNull()
            if (rs != null)
                listScales.addAll(rs)

//            launch(Dispatchers.IO) { sharedViewModel._activities.collect {  if (it != null) listActivities.addAll(it) } }
//            Log.w("async", "fetch others scal")
//            launch(Dispatchers.IO) { sharedViewModel._scales.collect {  if (it != null) listScales.addAll(it) } }
//            Log.w("async", "fetch others chal")
//            launch(Dispatchers.IO) { sharedViewModel._challenges.collect {  if (it != null) listChallenges.addAll(it) } }
//            Log.w("async", "end coroutine getalldata")
            //lifecycleScope.launch {updateHomeUI(AllData(listGoals, listActivities, listScales, listChallenges))}
            updateHomeUI(AllData(listGoals, listActivities, listScales, listChallenges))
        }
        Log.w("async", "before return data in shared view model")

    }

    @InternalCoroutinesApi
    private fun updateHomeUI(allData: AllData) {
        Log.w("async", "updateHomeUI: got ${allData.goals.size} goals and ${allData.scales.size} scales")
        val entryTypes = mutableListOf<EntryType>()
        binding.textView.text = "updateHomeUI: got ${allData.goals.size} goals and ${allData.scales.size} scales"
        Log.w("home", "finished loading")
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
        Log.w("async", "updateHomeUI: submit list: ${entryTypes.size}")
        if (entryTypes.size > 0){
            adapter.submitList(entryTypes)
            adapter.notifyDataSetChanged()
        }
    }

}