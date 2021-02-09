package de.wuapps.moredays.ui.home

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*

@InternalCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var scaleAdapter: ScaleAdapter
    private var date = Calendar.getInstance()


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
                db.scaleEntryDao,
                db.trophyDao
            )
        ).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewGoals.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.entry_type_item_width).toInt()
        )
        goalAdapter = GoalAdapter(homeViewModel)
        binding.recyclerViewGoals.adapter = goalAdapter
        binding.recyclerViewScales.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.entry_type_item_width).toInt()
        )
        scaleAdapter = ScaleAdapter(homeViewModel)
        binding.recyclerViewScales.adapter = scaleAdapter
        (activity as MainActivity).let{it.getFab().setOnClickListener{showAddOptions()}}
        subscribeUi()
        return binding.root
    }

    private fun showAddOptions(){

    }
    private fun subscribeUi() {
        homeViewModel.scales.observe(viewLifecycleOwner, {
            it?.let {
                scaleAdapter.submitList(it as MutableList<Scale>)
            }
        })
        homeViewModel.goalData.observe(viewLifecycleOwner, {
            it?.let {
                val data = it as MutableList<GoalEntry>
                goalAdapter.submitList(data)
                binding.circlesView.setCirclesData(data)
            }
        })
        homeViewModel.trophies.observe(viewLifecycleOwner, {
            it?.let {
                if (it.isNotEmpty()) {
                    val trophy = it.last()
                    Toast.makeText(application, "trophy", Toast.LENGTH_LONG).show()
                }
            }
        })
        homeViewModel.justReached100Percent.observe(viewLifecycleOwner, {
            if (it){
                binding.viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12))
                    .setPosition(-50f, binding.viewKonfetti.width + 50f, -50f, -50f)
                    .streamFor(300, 5000L)
            }
            homeViewModel.justReached100Percent.postValue(false)
        })
    }

}