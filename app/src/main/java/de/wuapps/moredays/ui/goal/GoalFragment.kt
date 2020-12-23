package de.wuapps.moredays.ui.goal

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack

import de.wuapps.moredays.R
import de.wuapps.moredays.data.repository.GoalRepository
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.GoalFragmentBinding
import de.wuapps.moredays.ui.activity.ActivityListViewModel
import de.wuapps.moredays.ui.icons.IconViewModel
import java.lang.Integer.parseInt

class GoalFragment : Fragment(), IconDialog.Callback {
    private val TAG = "GoalFragment"
    private var _binding: GoalFragmentBinding? = null
    private var selectedIconId:Int=-1

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: GoalViewModel
    private lateinit var iconViewModel: IconViewModel
    //private val viewModel: GoalViewModel by viewModels()
    //val vm: GoalViewModel by lazy { ViewModelProviders.of(this).get(GoalViewModel::class.java) }
    companion object {
        fun newInstance() = GoalFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GoalFragmentBinding.inflate(inflater, container, false)
        var view = binding.root

        val application = requireNotNull(this.activity).application
        val goalRepository = GoalRepository(MoreDaysDatabase.getInstance(application).goalDao)
        viewModel = GoalViewModel(goalRepository, -1, application)
        binding.goalViewModel = viewModel
        binding.setLifecycleOwner(this)
        binding.buttonSave.setOnClickListener({save()})
        binding.buttonDelete.setOnClickListener({delete()})
        val iconDialog = childFragmentManager.findFragmentByTag(viewModel.ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

    //iconDialog.show(requireActivity().supportFragmentManager
        binding.imageButtonIcon.setOnClickListener({iconDialog.show(childFragmentManager, viewModel.ICON_DIALOG_TAG)})

        iconViewModel = IconViewModel(application)
        selectedIconId = viewModel.goal.value?.symbol ?: -1
        return view

    }

    private fun delete(){
        //if (!viewModel.isNew) {
//            if (!viewModel.isUsed){
//                viewModel.delete()
//                return
//            }
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle("AlertDialog")
            alertDialog.setMessage("delete ...")
            alertDialog.setPositiveButton(
                "yes"
            ) { _, _ ->
                viewModel.delete()
            }
            alertDialog.setNegativeButton(
                "No"
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
       // }
    }
    private fun save(){
        val name = binding.editTextName.text.toString().trim()
        val pointsAsString = binding.editTextPoints.text.toString().trim()
        var points = 0
        val isNotOld = binding.checkboxIsNotOld.isChecked
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
            var goal = Goal()
            goal.name = name
            goal.points = points
            goal.isNotOld = isNotOld
            viewModel.insert(goal)
        }
        else {
            viewModel.goal.value!!.points = points //trick supports only one way binding!!!
            viewModel.update()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(GoalViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val iconDialogIconPack: IconPack?
        get() = iconViewModel.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        if (icons.size > 0) {
            selectedIconId = icons.get(0).id
            setIcon()
        }
    }

    private fun setIcon(){
        if (selectedIconId > 0)
            binding.imageButtonIcon.setImageDrawable(iconViewModel.iconPack.getIcon(selectedIconId)?.drawable)
    }
}