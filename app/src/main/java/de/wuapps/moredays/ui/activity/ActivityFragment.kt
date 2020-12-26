package de.wuapps.moredays.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.ActivityRepository
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.ActivityFragmentBinding
import java.lang.Integer.parseInt

class ActivityFragment : Fragment() {
    private val TAG = "ActivityFragment"
    private var _binding: ActivityFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: ActivityViewModel

    companion object {
        fun newInstance() = ActivityFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityFragmentBinding.inflate(inflater, container, false)
        var view = binding.root

        val arguments = ActivityFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(
            this, ActivityViewModelFactory(
                ActivityRepository(MoreDaysDatabase.getInstance(application).activityDao),
                GoalRepository(
                    MoreDaysDatabase.getInstance(
                        application
                    ).goalDao
                ),
                arguments.activityId
            )
        ).get(ActivityViewModel::class.java)
        binding.activityViewModel = viewModel

        binding.buttonSave.setOnClickListener({ save() })
        binding.buttonDelete.setOnClickListener({ delete() })
        var adapter = ArrayAdapter<Goal>(
            requireNotNull(this.activity),
            android.R.layout.simple_spinner_dropdown_item
        )

        viewModel.goals.observe(requireNotNull(this.activity), Observer { goals ->
            adapter.clear()
            adapter.addAll(goals)
        })
        binding.spinnerGoal.adapter = adapter
//        binding.spinnerGoal.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
//            Log.w("spinner", "onspinner $oldIndex $newIndex")
//            viewModel.goalSpinnerPosition.value = newIndex
//        }
//        val items = ArrayList<IconSpinnerItem>()
//        val context = this.activity
//        viewModel.goals.observe(requireNotNull(this.activity), Observer { goals ->
//            items.clear()
//            for(goal in goals)
//                items.add(IconSpinnerItem(text=goal.name, iconRes=R.drawable.ic_home))
//            binding.spinnerGoal.apply {
//                setSpinnerAdapter(IconSpinnerAdapter(this))
//                setItems(items)
//                getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)
//                //selectItemByIndex(0) // select an item initially.
//                lifecycleOwner = context
//            }
//        })


        //binding.spinnerGoal.setOnItemSelectedListener()
        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)
        return view

    }

    private fun delete() {
        if (!viewModel.isNew) {
            if (!viewModel.isUsed) {
                viewModel.delete()
                return
            }
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_activity))
            alertDialog.setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                viewModel.delete()
                findNavController().popBackStack()
            }
            alertDialog.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    private fun save() {
        val name = binding.editTextName.text.toString().trim()
        val pointsAsString = binding.editTextPoints.text.toString().trim()
        var points = 0
        val isOld = binding.checkboxIsOld.isChecked
        if (name.isNullOrBlank())
            binding.editTextName.setError(getString(R.string.warning_field_is_required))
        if (pointsAsString.isNullOrBlank())
            binding.editTextName.setError(getString(R.string.warning_field_is_required))
        try {
            points = parseInt(pointsAsString)
        } catch (e: NumberFormatException) {
            Log.e(TAG, e.message.toString())
        }
        if (viewModel.isNew) {
            var activity = Activity()
            activity.goalId = viewModel.goal?.uid ?: -1
            activity.name = name
            activity.points = points
            activity.isOld = isOld
            viewModel.insert(activity)
        } else {
            viewModel.activity.value!!.points = points //trick supports only one way binding!!!
            viewModel.activity.value!!.goalId = viewModel.goal!!.uid
            viewModel.update()
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}