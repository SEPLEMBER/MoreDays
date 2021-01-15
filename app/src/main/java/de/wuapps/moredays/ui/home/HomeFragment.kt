package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.wuapps.moredays.R
import de.wuapps.moredays.data.Analysis
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.databinding.FragmentHomeBinding
import de.wuapps.moredays.ui.ResponsiveGridLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@InternalCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var application: Application
    private lateinit var db: MoreDaysDatabase
    private lateinit var adapter: EntryTypeAdapter
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
                db.scaleEntryDao
            )
        ).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerViewEntryTypes.layoutManager = ResponsiveGridLayoutManager(
            requireNotNull(this.activity),
            resources.getDimension(R.dimen.entry_type_item_width).toInt()
        )
        adapter = EntryTypeAdapter(application)
        binding.recyclerViewEntryTypes.adapter = adapter
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        homeViewModel.entryTypes.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as MutableList<EntryType>)
                updateCircles()
            }
        })
    }

    private fun updateCircles() {
        binding.circlesView.setCirclesData(homeViewModel.scalesGoals.second)
    }

    public fun showInputDialog(entryType: EntryType){
        if (entryType is GoalEntry){

        }
        else if (entryType is ScaleEntry){

        }
    }

    private fun showInputDialogForGoalEntry(goalEntry: GoalEntry){
        val dialogBuilder = AlertDialog.Builder(context)
        val stringList: MutableList<String> = mutableListOf()
        val activities = goalEntry.getActivities().sortedBy { activity -> activity.points }
        activities.forEach { activity -> stringList.add(activity.toString()) }
        val values: Array<String> = stringList.toTypedArray()

        dialogBuilder.setTitle(goalEntry.getName()).setItems(values) { _, position ->
            val activity = activities[position]
            val analysis = Analysis()
//            GlobalScope.launch {
//                db.activityEntryDao.insert(
//                    ActivityEntry(activity)
//                )
//                CoroutineScope(Dispatchers.Main).launch {
//                    val job = analysis.analyzeTrophies(uid = 1L)
//                    if (job != null) {
//                        //better: viewmodel observes trophies with flow and livedata and fragment observes the latest trophy and shows a toast
//
//                        Toast.makeText(
//                            context,
//                            job.text,
//                            Toast.LENGTH_SHORT
//                        ).show();
//                    }
//
//            }



        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}