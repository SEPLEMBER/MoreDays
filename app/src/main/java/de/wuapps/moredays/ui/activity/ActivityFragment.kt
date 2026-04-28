package de.wuapps.moredays.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.FragmentActivityBinding
import de.wuapps.moredays.utilities.InjectorUtils
import de.wuapps.moredays.utilities.handleFab
import de.wuapps.moredays.utilities.hideBottomBar
import de.wuapps.moredays.utilities.showBottomBar
import java.lang.Integer.parseInt

class ActivityFragment : Fragment(), MenuProvider  {
    private val args: ActivityFragmentArgs by navArgs()
    private val viewModel: ActivityViewModel by viewModels {
        InjectorUtils.provideActivityViewModelFactory(requireActivity(), args.goal, args.activity)
    }
    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.activityViewModel = viewModel
        handleFab(viewModel.hasFab)
        hideBottomBar()
        return view

    }

    private fun delete() {
        if (viewModel.isNew) {
            viewModel.delete()
            view?.post {
                findNavController().popBackStack()
            }
        } else {
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(requireNotNull(this.activity))
            alertDialog.setTitle(getString(R.string.title_delete))
            alertDialog.setMessage(getString(R.string.question_delete_activity))
            alertDialog.setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                viewModel.delete()
                view?.post {
                    findNavController().popBackStack()
                }
            }
            alertDialog.setNegativeButton(
                getString(R.string.no)
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun save() {
        var isValid = true
        val name = binding.editTextName.text.toString().trim()
        val pointsAsString = binding.editTextPoints.text.toString().trim()
        var points = 0
        val isOld = binding.checkboxIsOld.isChecked
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        if (pointsAsString.isBlank()) {
            binding.editTextName.error = getString(R.string.warning_field_is_required)
            isValid = false
        }
        try {
            points = parseInt(pointsAsString)
        } catch (e: NumberFormatException) {
            binding.editTextPoints.error = getString(R.string.warning_not_an_int)
            isValid = false
        }
        if (!isValid)
            return
        viewModel.activity.name = name
        viewModel.activity.isOld = isOld
        viewModel.activity.points = points //trick supports only one way binding!!!
        viewModel.save()

        view?.post {
            findNavController().popBackStack()
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
            R.id.menu_help -> {
                showHelp()
                true
            }
            R.id.menu_delete -> {
                delete()
                true
            }
            R.id.menu_save -> {
                save()
                return true
            }
            else -> false
            }
        }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.help_delete_save_menu, menu)
        if (viewModel.isNew)
            menu.findItem(R.id.menu_delete).isVisible = false
    }

    private fun showHelp() {
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.textViewName,
                    getString(R.string.title_activities),
                    getString(R.string.help_add_activity)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30),
                TapTarget.forView(
                    binding.editTextPoints,
                    getString(R.string.title_activities),
                    getString(R.string.help_add_activity_points)
                )
                    .cancelable(false).transparentTarget(true).targetRadius(30)
            ).start()
    }

    override fun onResume() {
        super.onResume()
        hideBottomBar()
    }

    override fun onDestroyView() {
        showBottomBar()
        super.onDestroyView()
        _binding = null
    }
}