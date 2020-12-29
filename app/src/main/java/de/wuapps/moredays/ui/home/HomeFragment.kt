package de.wuapps.moredays.ui.home

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import de.wuapps.moredays.R
import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.ChallengeRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.data.repository.ScaleRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import de.wuapps.moredays.ui.SharedViewModel
import de.wuapps.moredays.ui.SharedViewModelFactory
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    @InternalCoroutinesApi
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private val adapter = EntryTypeAdapter()
    private var isLoading: Boolean = false
    private var date = Calendar.getInstance()
    private var goalsData: List<GoalAndRelatedData>? = null
    private  var scales: MutableList<Scale> = mutableListOf()

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
        update(0L)
        return binding.root
    }

    @InternalCoroutinesApi
    fun update(id: Long){
        lifecycleScope.launch {
            getAllData()
        }
    }
    @InternalCoroutinesApi
    suspend fun getAllData() {
        scales.clear()
        lifecycleScope.launch {
            val rs = sharedViewModel._scales.firstOrNull()
            if (rs != null) {
                scales.addAll(rs)
            }
            goalsData = GoalRepository(db.goalDao).allData.firstOrNull()
            if (goalsData != null)
                updateUI()
            //setObservers()
        }

    }

    @InternalCoroutinesApi
    private fun setObservers() {
        sharedViewModel.homeData.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                getAllData()
            }
        }
        )
    }

    @InternalCoroutinesApi
    private fun updateUI() {
        val entryTypes = mutableListOf<EntryType>()
        if (goalsData == null)
            return
        goalsData!!.forEach {
            entryTypes += GoalEntry(
                it,
                date,
                db.activityEntryDao,
                ::update
            )
        }
        scales.forEach {
            entryTypes += ScaleEntry(
                it,
                1F,
                db.scaleEntryDao
            )
        }
        if (entryTypes.isNotEmpty()) {
            adapter.submitList(entryTypes)
        }

    }

}